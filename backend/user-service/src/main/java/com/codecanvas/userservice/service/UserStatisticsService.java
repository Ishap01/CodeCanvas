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

    UserStatisticsResponse incrementFollowers(UUID userId);

    UserStatisticsResponse incrementFollowing(UUID userId);
}