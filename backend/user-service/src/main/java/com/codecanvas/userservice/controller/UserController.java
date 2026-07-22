package com.codecanvas.userservice.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codecanvas.userservice.dto.request.UserUpdateRequest;
import com.codecanvas.userservice.dto.response.ApiResponse;
import com.codecanvas.userservice.dto.response.UserResponse;
import com.codecanvas.userservice.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // GET ALL USERS
    // GET http://localhost:8081/api/users
    @GetMapping
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers();
    }

    // GET USER BY ID
    // GET http://localhost:8081/api/users/{userId}
    @GetMapping("/{userId}")
    public UserResponse getUserById(
            @PathVariable UUID userId) {

        return userService.getUserById(userId);
    }

    @GetMapping("/profile")
    public UserResponse getProfile() {
        return userService.getProfile();
    }

    // UPDATE USER
    // PUT http://localhost:8081/api/users/profile
    @PutMapping("/profile")
    public ApiResponse updateProfile(
            @RequestBody UserUpdateRequest request) {

        return userService.updateProfile(request);
    }

    // DELETE USER
    // DELETE http://localhost:8081/api/users/profile
    @DeleteMapping("/profile")
    public ApiResponse deleteProfile() {
        return userService.deleteProfile();
    }
}