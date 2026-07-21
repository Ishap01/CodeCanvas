package com.codecanvas.userservice.service.impl;

import java.time.LocalDateTime;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codecanvas.userservice.dto.request.ForgotPasswordRequest;
import com.codecanvas.userservice.dto.request.LoginRequest;
import com.codecanvas.userservice.dto.request.RegisterRequest;
import com.codecanvas.userservice.dto.response.ApiResponse;
import com.codecanvas.userservice.dto.response.AuthResponse;
import com.codecanvas.userservice.entity.Role;
import com.codecanvas.userservice.entity.User;
import com.codecanvas.userservice.repository.UserRepository;
import com.codecanvas.userservice.service.AuthService;
import com.codecanvas.userservice.service.JwtService;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

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

            userRepository.save(user);

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

    @Override
    public ApiResponse forgotPassword(
            ForgotPasswordRequest request) {

        return new ApiResponse(
                false,
                "Forgot password functionality is not implemented yet"
        );
    }
}