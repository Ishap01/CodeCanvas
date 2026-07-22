package com.codecanvas.userservice.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.codecanvas.userservice.dto.response.ApiResponse;
import com.codecanvas.userservice.service.FollowService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    @PostMapping("/follow/{userId}")
    public ResponseEntity<ApiResponse> followUser(@PathVariable UUID userId) {

        ApiResponse response = followService.followUser(userId);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/unfollow/{userId}")
    public ResponseEntity<ApiResponse> unfollowUser(@PathVariable UUID userId) {

        ApiResponse response = followService.unfollowUser(userId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}/followers/count")
    public ResponseEntity<Long> getFollowersCount(@PathVariable UUID userId) {

        return ResponseEntity.ok(
                followService.getFollowersCount(userId)
        );
    }

    @GetMapping("/{userId}/following/count")
    public ResponseEntity<Long> getFollowingCount(@PathVariable UUID userId) {

        return ResponseEntity.ok(
                followService.getFollowingCount(userId)
        );
    }



}