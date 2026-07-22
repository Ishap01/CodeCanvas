package com.codecanvas.userservice.service.impl;

import com.codecanvas.userservice.dto.request.*;
import com.codecanvas.userservice.dto.response.ApiResponse;
import com.codecanvas.userservice.dto.response.AuthResponse;
import com.codecanvas.userservice.entity.PasswordResetOtp;
import com.codecanvas.userservice.entity.Role;
import com.codecanvas.userservice.entity.User;
import com.codecanvas.userservice.exception.*;
import com.codecanvas.userservice.repository.PasswordResetOtpRepository;
import com.codecanvas.userservice.repository.UserRepository;
import com.codecanvas.userservice.service.AuthService;
import com.codecanvas.userservice.service.EmailService;
import com.codecanvas.userservice.security.JwtService;
import com.codecanvas.userservice.util.OtpGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordResetOtpRepository otpRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public ApiResponse register(RegisterRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            return new ApiResponse(false, "Username already exists");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            return ApiResponse.builder()
                    .success(false)
                    .message("Email already exists")
                    .build();
        }

        if (userRepository.existsByMobileNumber(request.getMobileNumber())) {
            return new ApiResponse(
                    false,
                    "Mobile number already exists"
            );
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            return new ApiResponse(false, "Passwords do not match");
        }

        User user = new User();

        user.setFullName(request.getFullName());
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setMobileNumber(request.getMobileNumber());
        user.setRole(Role.USER);
        user.setBio("");
        user.setProfileImage("");

        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        user.setPassword(passwordEncoder.encode(request.getPassword()));

        userRepository.save(user);

        return new ApiResponse(true, "User registered successfully");
    }

    @Override
    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElse(null);

        System.out.println("User found: " + user);


        if (user == null) {
            return new AuthResponse(
                    false,
                    "Invalid Username or Password",
                    null
            );
        }

        boolean isPasswordCorrect =
                passwordEncoder.matches(
                        request.getPassword(),
                        user.getPassword()
                );

        System.out.println("Password match: " + isPasswordCorrect);

        if (!isPasswordCorrect) {
            return new AuthResponse(
                    false,
                    "Invalid Username or Password",
                    null
            );
        }

        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        String token = jwtService.generateToken(user);

        return new AuthResponse(
                true,
                "Login Successful",
                token
        );
    }

    @Override
    public void forgotPassword(ForgotPasswordRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        otpRepository.deleteByEmail(user.getEmail());

        String otp = OtpGenerator.generateOtp();

        PasswordResetOtp passwordResetOtp = PasswordResetOtp.builder()
                .email(user.getEmail())
                .otp(otp)
                .expiryTime(LocalDateTime.now().plusMinutes(5))
                .verified(false)
                .build();

        otpRepository.save(passwordResetOtp);

        emailService.sendOtpEmail(user.getEmail(), otp);
    }

    @Override
    public String verifyOtp(VerifyOtpRequest request) {

        PasswordResetOtp otpEntity = otpRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid OTP"));

        if (otpEntity.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new OtpExpiredException("OTP has expired");
        }

        if (!otpEntity.getOtp().equals(request.getOtp())) {
            throw new InvalidOtpException("Invalid OTP");
        }

        otpEntity.setVerified(true);

        otpRepository.save(otpEntity);

        return "OTP verified successfully.";
    }

    @Override
    public String resetPassword(ResetPasswordRequest request) {

        PasswordResetOtp otpEntity = otpRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Please verify OTP first"));

        if (!otpEntity.isVerified()) {
            throw new RuntimeException("OTP not verified");
        }

        if (otpEntity.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP expired");
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        userRepository.save(user);

        otpRepository.delete(otpEntity);

        return "Password reset successfully.";
    }

    @Override
    public String changePassword(String email,ChangePasswordRequest request) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new InvalidPasswordException("Current password is incorrect");
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new PasswordMismatchException("New password and confirm password do not match");
        }

        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new SamePasswordException("New password must be different from current password");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);

        return "Password changed successfully.";
    }

}

