package com.codecanvas.userservice.service;

import java.util.UUID;

import com.codecanvas.userservice.dto.response.UserStatisticsResponse;

public interface UserStatisticsService {

    UserStatisticsResponse getStatisticsByUserId(UUID userId);

    UserStatisticsResponse incrementProjects(UUID userId);

    UserStatisticsResponse incrementSnippets(UUID userId);

    UserStatisticsResponse incrementViews(UUID userId);

    UserStatisticsResponse incrementLikes(UUID userId);

    UserStatisticsResponse incrementFavorites(UUID userId);

    void increaseFollowers(UUID userId);

    void decreaseFollowers(UUID userId);

    void increaseFollowing(UUID userId);

    void decreaseFollowing(UUID userId);
}