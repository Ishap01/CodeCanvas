package com.codecanvas.userservice.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.codecanvas.userservice.dto.request.UserUpdateRequest;
import com.codecanvas.userservice.dto.response.ApiResponse;
import com.codecanvas.userservice.dto.response.UserResponse;
import com.codecanvas.userservice.service.UserService;
import org.springframework.web.multipart.MultipartFile;

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

    //user profile image
    @PutMapping(value = "/profile/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserResponse> uploadProfileImage(
            @RequestParam("image") MultipartFile image) {

        return ResponseEntity.ok(userService.uploadProfileImage(image));
    }
}