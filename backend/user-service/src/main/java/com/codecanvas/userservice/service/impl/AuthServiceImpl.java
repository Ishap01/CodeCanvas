package com.codecanvas.userservice.service.impl;

import java.time.LocalDateTime;

import com.codecanvas.userservice.exception.OtpExpiredException;
import com.codecanvas.userservice.exception.InvalidOtpException;
import com.codecanvas.userservice.exception.SamePasswordException;
import com.codecanvas.userservice.exception.UserNotFoundException;
import com.codecanvas.userservice.exception.InvalidPasswordException;
import com.codecanvas.userservice.exception.PasswordMismatchException;
import com.codecanvas.userservice.dto.request.VerifyOtpRequest;
import com.codecanvas.userservice.dto.request.ChangePasswordRequest;
import com.codecanvas.userservice.dto.request.ResetPasswordRequest;
import com.codecanvas.userservice.dto.request.ForgotPasswordRequest;
import com.codecanvas.userservice.dto.request.LoginRequest;
import com.codecanvas.userservice.dto.request.RegisterRequest;
import com.codecanvas.userservice.dto.response.ApiResponse;
import com.codecanvas.userservice.dto.response.AuthResponse;
import com.codecanvas.userservice.entity.PasswordResetOtp;
import com.codecanvas.userservice.entity.Role;
import com.codecanvas.userservice.entity.User;
import com.codecanvas.userservice.repository.PasswordResetOtpRepository;
import com.codecanvas.userservice.repository.UserRepository;
import com.codecanvas.userservice.service.AuthService;
import com.codecanvas.userservice.service.EmailService;
import com.codecanvas.userservice.util.OtpGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordResetOtpRepository otpRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

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

        User user = userRepository.findByUsername(request.getUsername())
                .orElse(null);

        if (user == null) {
            return new AuthResponse(false,
                    "Invalid Username or Password",
                    null);
        }

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())) {

            return new AuthResponse(false,
                    "Invalid Username or Password",
                    null);
        }

        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        return new AuthResponse(
                true,
                "Login Successful",
                null
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
    public String changePassword(ChangePasswordRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
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

