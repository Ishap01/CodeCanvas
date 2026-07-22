package com.codecanvas.userservice.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.codecanvas.userservice.dto.response.UserStatisticsResponse;
import com.codecanvas.userservice.service.UserStatisticsService;

@RestController
@RequestMapping("/api/statistics")
public class UserStatisticsController {

    private final UserStatisticsService statisticsService;

    public UserStatisticsController(
            UserStatisticsService statisticsService) {

        this.statisticsService = statisticsService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserStatisticsResponse> getStatistics(
            @PathVariable UUID userId) {

        return ResponseEntity.ok(
                statisticsService.getStatisticsByUserId(userId)
        );
    }

    @PutMapping("/{userId}/projects")
    public ResponseEntity<UserStatisticsResponse> incrementProjects(
            @PathVariable UUID userId) {

        return ResponseEntity.ok(
                statisticsService.incrementProjects(userId)
        );
    }

    @PutMapping("/{userId}/snippets")
    public ResponseEntity<UserStatisticsResponse> incrementSnippets(
            @PathVariable UUID userId) {

        return ResponseEntity.ok(
                statisticsService.incrementSnippets(userId)
        );
    }

    @PutMapping("/{userId}/views")
    public ResponseEntity<UserStatisticsResponse> incrementViews(
            @PathVariable UUID userId) {

        return ResponseEntity.ok(
                statisticsService.incrementViews(userId)
        );
    }

    @PutMapping("/{userId}/likes")
    public ResponseEntity<UserStatisticsResponse> incrementLikes(
            @PathVariable UUID userId) {

        return ResponseEntity.ok(
                statisticsService.incrementLikes(userId)
        );
    }

    @PutMapping("/{userId}/favorites")
    public ResponseEntity<UserStatisticsResponse> incrementFavorites(
            @PathVariable UUID userId) {

        return ResponseEntity.ok(
                statisticsService.incrementFavorites(userId)
        );
    }

    @PutMapping("/{userId}/followers")
    public ResponseEntity<UserStatisticsResponse> incrementFollowers(
            @PathVariable UUID userId) {

        return ResponseEntity.ok(
                statisticsService.incrementFollowers(userId)
        );
    }

    @PutMapping("/{userId}/following")
    public ResponseEntity<UserStatisticsResponse> incrementFollowing(
            @PathVariable UUID userId) {

        return ResponseEntity.ok(
                statisticsService.incrementFollowing(userId)
        );
    }
}