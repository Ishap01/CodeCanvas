package com.codecanvas.userservice.service.impl;

import java.time.LocalDateTime;

import com.codecanvas.userservice.kafka.event.UserRegisteredEvent;
import com.codecanvas.userservice.kafka.mapper.UserEventMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codecanvas.userservice.dto.request.ChangePasswordRequest;
import com.codecanvas.userservice.dto.request.ForgotPasswordRequest;
import com.codecanvas.userservice.dto.request.LoginRequest;
import com.codecanvas.userservice.dto.request.RegisterRequest;
import com.codecanvas.userservice.dto.request.ResetPasswordRequest;
import com.codecanvas.userservice.dto.request.VerifyOtpRequest;
import com.codecanvas.userservice.dto.response.ApiResponse;
import com.codecanvas.userservice.dto.response.AuthResponse;
import com.codecanvas.userservice.entity.PasswordResetOtp;
import com.codecanvas.userservice.entity.Role;
import com.codecanvas.userservice.entity.User;
import com.codecanvas.userservice.entity.UserStatistics;
import com.codecanvas.userservice.exception.InvalidOtpException;
import com.codecanvas.userservice.exception.InvalidPasswordException;
import com.codecanvas.userservice.exception.OtpExpiredException;
import com.codecanvas.userservice.exception.PasswordMismatchException;
import com.codecanvas.userservice.exception.SamePasswordException;
import com.codecanvas.userservice.exception.UserNotFoundException;
import com.codecanvas.userservice.repository.PasswordResetOtpRepository;
import com.codecanvas.userservice.repository.UserRepository;
import com.codecanvas.userservice.repository.UserStatisticsRepository;
import com.codecanvas.userservice.service.AuthService;
import com.codecanvas.userservice.service.EmailService;
import com.codecanvas.userservice.security.JwtService;
import com.codecanvas.userservice.util.OtpGenerator;
import com.codecanvas.userservice.kafka.producer.UserEventProducer;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final UserStatisticsRepository userStatisticsRepository;
    private final PasswordResetOtpRepository otpRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserEventProducer userEventProducer;
    private final UserEventMapper userEventMapper;

    // =========================================================
    // REGISTER
    // =========================================================

    @Override
    @Transactional
    public ApiResponse register(RegisterRequest request) {

        if (request == null) {
            return new ApiResponse(
                    false,
                    "Request body is required"
            );
        }

        if (request.getFullName() == null
                || request.getFullName().trim().isEmpty()) {

            return new ApiResponse(
                    false,
                    "Full name is required"
            );
        }

        if (request.getEmail() == null
                || request.getEmail().trim().isEmpty()) {

            return new ApiResponse(
                    false,
                    "Email is required"
            );
        }

        if (request.getUsername() == null
                || request.getUsername().trim().isEmpty()) {

            return new ApiResponse(
                    false,
                    "Username is required"
            );
        }

        if (request.getMobileNumber() == null
                || !request.getMobileNumber()
                .trim()
                .matches("\\d{10}")) {

            return new ApiResponse(
                    false,
                    "Mobile number must contain exactly 10 digits"
            );
        }

        if (request.getPassword() == null
                || request.getPassword().isBlank()) {

            return new ApiResponse(
                    false,
                    "Password is required"
            );
        }

        if (request.getPassword().length() < 6) {
            return new ApiResponse(
                    false,
                    "Password must contain at least 6 characters"
            );
        }

        if (request.getConfirmPassword() == null
                || !request.getPassword()
                .equals(request.getConfirmPassword())) {

            return new ApiResponse(
                    false,
                    "Passwords do not match"
            );
        }

        String fullName =
                request.getFullName().trim();

        String email =
                request.getEmail()
                        .trim()
                        .toLowerCase();

        String username =
                request.getUsername()
                        .trim()
                        .toLowerCase();

        String mobileNumber =
                request.getMobileNumber().trim();

        if (userRepository.existsByUsername(username)) {
            return new ApiResponse(
                    false,
                    "Username already exists"
            );
        }

        if (userRepository.existsByEmail(email)) {
            return new ApiResponse(
                    false,
                    "Email already exists"
            );
        }

        if (userRepository.existsByMobileNumber(mobileNumber)) {
            return new ApiResponse(
                    false,
                    "Mobile number already exists"
            );
        }

        try {

            User user = new User();

            user.setFullName(fullName);
            user.setEmail(email);
            user.setUsername(username);
            user.setMobileNumber(mobileNumber);


            user.setPassword(
                    passwordEncoder.encode(
                            request.getPassword()
                    )
            );

            user.setRole(Role.USER);
            user.setBio("");
            user.setProfileImage("");

            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());

            // User ko database me save kiya
            User savedUser = userRepository.save(user);

            // Register hote hi statistics row automatically create hogi
            UserStatistics statistics = new UserStatistics();

            statistics.setUser(savedUser);
            statistics.setTotalProjects(0);
            statistics.setTotalSnippets(0);
            statistics.setTotalViews(0);
            statistics.setTotalLikes(0);
            statistics.setTotalFavorites(0);
            statistics.setFollowers(0);
            statistics.setFollowing(0);

            userStatisticsRepository.save(statistics);

            /*
             * =========================================================
             * KAFKA EVENT
             * Publish User Registered Event
             * =========================================================
             */
            UserRegisteredEvent event =
                    userEventMapper.toUserRegisteredEvent(savedUser);

            userEventProducer.publishUserRegisteredEvent(event);

            return new ApiResponse(
                    true,
                    "User registered successfully"
            );

        } catch (DataIntegrityViolationException exception) {

            return new ApiResponse(
                    false,
                    "Email, username or mobile number already exists"
            );

        } catch (Exception exception) {

            exception.printStackTrace();

            return new ApiResponse(
                    false,
                    "Unable to register user: "
                            + exception.getMessage()
            );
        }
    }

    // =========================================================
    // LOGIN
    // =========================================================

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {

        if (request == null
                || request.getEmail() == null
                || request.getEmail().isBlank()
                || request.getPassword() == null
                || request.getPassword().isBlank()) {

            return new AuthResponse(
                    false,
                    "Email and password are required",
                    null
            );
        }

        String email =
                request.getEmail()
                        .trim()
                        .toLowerCase();

        User user = userRepository
                .findByEmail(email)
                .orElse(null);

        if (user == null) {
            return new AuthResponse(
                    false,
                    "Invalid email or password",
                    null
            );
        }

        boolean passwordCorrect =
                passwordEncoder.matches(
                        request.getPassword(),
                        user.getPassword()
                );

        if (!passwordCorrect) {
            return new AuthResponse(
                    false,
                    "Invalid email or password",
                    null
            );
        }

        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        String token =
                jwtService.generateToken(user);

        return new AuthResponse(
                true,
                "Login successful",
                token
        );
    }

    // =========================================================
    // FORGOT PASSWORD
    // =========================================================

    @Override
    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {

        User user = userRepository
                .findByEmail(request.getEmail().trim().toLowerCase())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        String otp = OtpGenerator.generateOtp();

        PasswordResetOtp passwordResetOtp = otpRepository
                .findByEmail(user.getEmail())
                .orElse(
                        PasswordResetOtp.builder()
                                .email(user.getEmail())
                                .build()
                );

        passwordResetOtp.setOtp(otp);
        passwordResetOtp.setExpiryTime(LocalDateTime.now().plusMinutes(5));
        passwordResetOtp.setVerified(false);

        otpRepository.save(passwordResetOtp);

        emailService.sendOtpEmail(user.getEmail(), otp);
    }

    // =========================================================
    // VERIFY OTP
    // =========================================================

    @Override
    @Transactional
    public String verifyOtp(VerifyOtpRequest request) {

        PasswordResetOtp otpEntity =
                otpRepository
                        .findByEmail(
                                request.getEmail()
                                        .trim()
                                        .toLowerCase()
                        )
                        .orElseThrow(() ->
                                new InvalidOtpException(
                                        "Invalid OTP"
                                )
                        );

        if (otpEntity.getExpiryTime()
                .isBefore(LocalDateTime.now())) {

            throw new OtpExpiredException(
                    "OTP has expired"
            );
        }

        if (!otpEntity.getOtp()
                .equals(request.getOtp())) {

            throw new InvalidOtpException(
                    "Invalid OTP"
            );
        }

        otpEntity.setVerified(true);
        otpRepository.save(otpEntity);

        return "OTP verified successfully";
    }

    // =========================================================
    // RESET PASSWORD
    // =========================================================

    @Override
    @Transactional
    public String resetPassword(
            ResetPasswordRequest request) {

        String email =
                request.getEmail()
                        .trim()
                        .toLowerCase();

        PasswordResetOtp otpEntity =
                otpRepository
                        .findByEmail(email)
                        .orElseThrow(() ->
                                new InvalidOtpException(
                                        "Please verify OTP first"
                                )
                        );

        if (!otpEntity.isVerified()) {
            throw new InvalidOtpException(
                    "OTP is not verified"
            );
        }

        if (otpEntity.getExpiryTime()
                .isBefore(LocalDateTime.now())) {

            throw new OtpExpiredException(
                    "OTP has expired"
            );
        }

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found"
                        )
                );

        user.setPassword(
                passwordEncoder.encode(
                        request.getNewPassword()
                )
        );

        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);

        // Password reset ke baad OTP delete
        otpRepository.delete(otpEntity);

        return "Password reset successfully";
    }

    // =========================================================
    // CHANGE PASSWORD
    // =========================================================

    @Override
    @Transactional
    public String changePassword(
            String email,
            ChangePasswordRequest request) {

        User user = userRepository
                .findByEmail(
                        email.trim().toLowerCase()
                )
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found"
                        )
                );

        if (!passwordEncoder.matches(
                request.getCurrentPassword(),
                user.getPassword())) {

            throw new InvalidPasswordException(
                    "Current password is incorrect"
            );
        }

        if (!request.getNewPassword()
                .equals(request.getConfirmPassword())) {

            throw new PasswordMismatchException(
                    "New password and confirm password do not match"
            );
        }

        if (passwordEncoder.matches(
                request.getNewPassword(),
                user.getPassword())) {

            throw new SamePasswordException(
                    "New password must be different from current password"
            );
        }

        user.setPassword(
                passwordEncoder.encode(
                        request.getNewPassword()
                )
        );

        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);

        return "Password changed successfully";
    }
}