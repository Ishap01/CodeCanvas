package com.codecanvas.userservice.service;

import java.util.List;
import java.util.UUID;

import com.codecanvas.userservice.dto.request.UserUpdateRequest;
import com.codecanvas.userservice.dto.response.ApiResponse;
import com.codecanvas.userservice.dto.response.UserResponse;

public interface UserService {

    List<UserResponse> getAllUsers();

    UserResponse getUserById(UUID userId);

    ApiResponse updateUser(
            UUID userId,
            UserUpdateRequest request
    );

    ApiResponse deleteUser(UUID userId);
}