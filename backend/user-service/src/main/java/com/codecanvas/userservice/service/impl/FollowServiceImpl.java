package com.codecanvas.userservice.service.impl;

import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.codecanvas.userservice.dto.response.ApiResponse;
import com.codecanvas.userservice.entity.Follow;
import com.codecanvas.userservice.entity.User;
import com.codecanvas.userservice.exception.UserNotFoundException;
import com.codecanvas.userservice.repository.FollowRepository;
import com.codecanvas.userservice.repository.UserRepository;
import com.codecanvas.userservice.service.FollowService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FollowServiceImpl implements FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    @Override
    public ApiResponse followUser(UUID followingUserId) {

        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        String email = authentication.getName();

        User follower = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Logged in user not found."));

        User following = userRepository.findById(followingUserId)
                .orElseThrow(() -> new UserNotFoundException("User to follow not found."));

        if (follower.getUserId().equals(following.getUserId())) {
            return new ApiResponse(false, "You cannot follow yourself.");
        }

        boolean alreadyFollowing = followRepository.existsByFollowerIdAndFollowingId(
                follower.getUserId(),
                following.getUserId());

        if (alreadyFollowing) {
            return new ApiResponse(false, "You are already following this user.");
        }

        Follow follow = Follow.builder()
                .followerId(follower.getUserId())
                .followingId(following.getUserId())
                .build();

        followRepository.save(follow);

        return new ApiResponse(true, "User followed successfully.");
    }

    @Override
    public ApiResponse unfollowUser(UUID followingUserId) {

        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        String email = authentication.getName();

        User follower = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Logged in user not found."));

        User following = userRepository.findById(followingUserId)
                .orElseThrow(() -> new UserNotFoundException("User not found."));

        System.out.println("Follower ID : " + follower.getUserId());
        System.out.println("Following ID: " + following.getUserId());

        Follow follow = followRepository
                .findByFollowerIdAndFollowingId(
                        follower.getUserId(),
                        following.getUserId())
                .orElseThrow(() -> new RuntimeException("You are not following this user."));

        System.out.println("Follow Record Found: " + follow.getFollowId());

        followRepository.delete(follow);

        System.out.println("Delete executed");

        return new ApiResponse(true, "User unfollowed successfully.");
    }

    @Override
    public long getFollowersCount(UUID userId) {

        return followRepository.countByFollowingId(userId);
    }

    @Override
    public long getFollowingCount(UUID userId) {

        return followRepository.countByFollowerId(userId);
    }

}