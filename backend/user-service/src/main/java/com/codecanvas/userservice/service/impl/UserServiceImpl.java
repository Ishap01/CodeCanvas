package com.codecanvas.userservice.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.codecanvas.userservice.service.CloudinaryService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
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
    private final CloudinaryService cloudinaryService;

    public UserServiceImpl(UserRepository userRepository, CloudinaryService cloudinaryService) {
        this.userRepository = userRepository;
        this.cloudinaryService = cloudinaryService;
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
                user.getProfileImage(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getLastLogin()
        );
    }

    @Override
    @Transactional
    public UserResponse uploadProfileImage(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Please select an image to upload."
            );
        }

        String contentType = file.getContentType();

        if (contentType == null ||
                (!contentType.equals("image/jpeg")
                        && !contentType.equals("image/png")
                        && !contentType.equals("image/webp"))) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Only JPG, PNG and WEBP images are allowed."
            );
        }

        long maxSize = 5 * 1024 * 1024;

        if (file.getSize() > maxSize) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Image size must not exceed 5 MB."
            );
        }

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

        // Keep old image URL
        String oldImage = user.getProfileImage();

        // Upload new image first
        String newImage = cloudinaryService.uploadImage(file);

        // Save new image URL
        user.setProfileImage(newImage);

        userRepository.save(user);

        // Delete old image only after successful save
        if (oldImage != null && !oldImage.isBlank()) {
            try {
                cloudinaryService.deleteImage(oldImage);
            } catch (Exception e) {
                // Ignore deletion failures
                // User still has the new profile image
            }
        }

        return convertToUserResponse(user);
    }


}