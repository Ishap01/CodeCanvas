package com.codecanvas.userservice.service;

import com.codecanvas.userservice.dto.response.ApiResponse;

import java.util.UUID;

public interface FollowService {

    ApiResponse followUser(UUID followingUserId);

    ApiResponse unfollowUser(UUID followingUserId);

    long getFollowersCount(UUID userId);

    long getFollowingCount(UUID userId);
}