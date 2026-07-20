package com.codecanvas.userservice.service.impl;


import java.time.LocalDateTime;

import com.codecanvas.userservice.entity.Role;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.codecanvas.userservice.dto.request.ForgotPasswordRequest;
import com.codecanvas.userservice.dto.request.LoginRequest;
import com.codecanvas.userservice.dto.request.RegisterRequest;
import com.codecanvas.userservice.dto.response.ApiResponse;
import com.codecanvas.userservice.dto.response.AuthResponse;
import com.codecanvas.userservice.entity.User;
import com.codecanvas.userservice.repository.UserRepository;
import com.codecanvas.userservice.service.AuthService;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

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
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setMobileNumber(request.getMobileNumber());

        user.setRole(Role.USER);

        user.setBio("");
        user.setProfileImage("");

        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        // Password ko BCrypt hash me convert karke database me save karega
        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        userRepository.save(user);

        return new ApiResponse(
                true,
                "User registered successfully"
        );
    }

    @Override
    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByUsername(request.getUsername())
                .orElse(null);

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

        if (!isPasswordCorrect) {
            return new AuthResponse(
                    false,
                    "Invalid Username or Password",
                    null
            );
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
    public ApiResponse forgotPassword(ForgotPasswordRequest request) {


        return null;
    }
}