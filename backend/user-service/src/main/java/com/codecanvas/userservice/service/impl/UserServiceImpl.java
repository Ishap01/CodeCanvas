package com.codecanvas.userservice.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.codecanvas.userservice.dto.request.UserUpdateRequest;
import com.codecanvas.userservice.dto.response.ApiResponse;
import com.codecanvas.userservice.dto.response.UserResponse;
import com.codecanvas.userservice.entity.User;
import com.codecanvas.userservice.repository.UserRepository;
import com.codecanvas.userservice.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<UserResponse> getAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(this::convertToUserResponse)
                .toList();
    }

    @Override
    public UserResponse getUserById(UUID userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "User not found"
                        )
                );

        return convertToUserResponse(user);
    }

    @Override
    public UserResponse getProfile() {

        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "User not found"
                        ));

        return convertToUserResponse(user);
    }

    @Override
    @Transactional
    public ApiResponse updateProfile(UserUpdateRequest request) {

        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElse(null);

        if (user == null) {
            return new ApiResponse(
                    false,
                    "User not found"
            );
        }

        if (request == null) {
            return new ApiResponse(
                    false,
                    "Request body is required"
            );
        }

        if (request.getFullName() == null
                || request.getFullName().isBlank()) {

            return new ApiResponse(
                    false,
                    "Full name is required"
            );
        }

        if (request.getUsername() == null
                || request.getUsername().isBlank()) {

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

        String fullName = request.getFullName().trim();
        String username = request.getUsername().trim().toLowerCase();
        String mobileNumber = request.getMobileNumber().trim();

        Optional<User> sameUsername =
                userRepository.findByUsername(username);

        if (sameUsername.isPresent()
                && !sameUsername.get()
                .getUserId()
                .equals(user.getUserId())) {

            return new ApiResponse(
                    false,
                    "Username already exists"
            );
        }

        Optional<User> sameMobile =
                userRepository.findByMobileNumber(mobileNumber);

        if (sameMobile.isPresent()
                && !sameMobile.get()
                .getUserId()
                .equals(user.getUserId())) {

            return new ApiResponse(
                    false,
                    "Mobile number already exists"
            );
        }

        user.setFullName(fullName);
        user.setUsername(username);
        user.setMobileNumber(mobileNumber);

        userRepository.save(user);

        return new ApiResponse(
                true,
                "Profile updated successfully"
        );
    }

    @Override
    @Transactional
    public ApiResponse deleteProfile() {

        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElse(null);

        if (user == null) {
            return new ApiResponse(
                    false,
                    "User not found"
            );
        }

        userRepository.delete(user);

        return new ApiResponse(
                true,
                "Profile deleted successfully"
        );
    }

    private UserResponse convertToUserResponse(User user) {

        return new UserResponse(
                user.getUserId(),
                user.getFullName(),
                user.getMobileNumber(),
                user.getUsername(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getLastLogin()
        );
    }
}