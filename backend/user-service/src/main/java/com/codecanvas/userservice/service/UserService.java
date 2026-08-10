package com.codecanvas.userservice.service;

import java.util.List;
import java.util.UUID;

import com.codecanvas.userservice.dto.request.UserUpdateRequest;
import com.codecanvas.userservice.dto.response.ApiResponse;
import com.codecanvas.userservice.dto.response.PublicProfileResponse;
import com.codecanvas.userservice.dto.response.UserResponse;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

    List<UserResponse> getAllUsers();

    UserResponse getUserById(UUID userId);

    UserResponse getProfile();

    ApiResponse updateProfile(
            UserUpdateRequest request);

    ApiResponse deleteProfile();

    UserResponse uploadProfileImage(MultipartFile file);

    PublicProfileResponse getPublicProfile(String username);


}