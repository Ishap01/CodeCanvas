package com.codecanvas.userservice.service.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codecanvas.userservice.dto.response.UserStatisticsResponse;
import com.codecanvas.userservice.entity.UserStatistics;
import com.codecanvas.userservice.repository.UserStatisticsRepository;
import com.codecanvas.userservice.service.UserStatisticsService;

@Service
public class UserStatisticsServiceImpl
        implements UserStatisticsService {

    private final UserStatisticsRepository statisticsRepository;

    public UserStatisticsServiceImpl(
            UserStatisticsRepository statisticsRepository) {

        this.statisticsRepository = statisticsRepository;
    }

    @Override
    public UserStatisticsResponse getStatisticsByUserId(UUID userId) {

        UserStatistics statistics = getStatistics(userId);

        return convertToResponse(statistics);
    }

    @Override
    @Transactional
    public UserStatisticsResponse incrementProjects(UUID userId) {

        UserStatistics statistics = getStatistics(userId);

        statistics.setTotalProjects(
                statistics.getTotalProjects() + 1
        );

        UserStatistics savedStatistics =
                statisticsRepository.save(statistics);

        return convertToResponse(savedStatistics);
    }

    @Override
    @Transactional
    public UserStatisticsResponse incrementSnippets(UUID userId) {

        UserStatistics statistics = getStatistics(userId);

        statistics.setTotalSnippets(
                statistics.getTotalSnippets() + 1
        );

        UserStatistics savedStatistics =
                statisticsRepository.save(statistics);

        return convertToResponse(savedStatistics);
    }

    @Override
    @Transactional
    public UserStatisticsResponse incrementViews(UUID userId) {

        UserStatistics statistics = getStatistics(userId);

        statistics.setTotalViews(
                statistics.getTotalViews() + 1
        );

        UserStatistics savedStatistics =
                statisticsRepository.save(statistics);

        return convertToResponse(savedStatistics);
    }

    @Override
    @Transactional
    public UserStatisticsResponse incrementLikes(UUID userId) {

        UserStatistics statistics = getStatistics(userId);

        statistics.setTotalLikes(
                statistics.getTotalLikes() + 1
        );

        UserStatistics savedStatistics =
                statisticsRepository.save(statistics);

        return convertToResponse(savedStatistics);
    }

    @Override
    @Transactional
    public UserStatisticsResponse incrementFavorites(UUID userId) {

        UserStatistics statistics = getStatistics(userId);

        statistics.setTotalFavorites(
                statistics.getTotalFavorites() + 1
        );

        UserStatistics savedStatistics =
                statisticsRepository.save(statistics);

        return convertToResponse(savedStatistics);
    }

    @Override
    @Transactional
    public UserStatisticsResponse incrementFollowers(UUID userId) {

        UserStatistics statistics = getStatistics(userId);

        statistics.setFollowers(
                statistics.getFollowers() + 1
        );

        UserStatistics savedStatistics =
                statisticsRepository.save(statistics);

        return convertToResponse(savedStatistics);
    }

    @Override
    @Transactional
    public UserStatisticsResponse incrementFollowing(UUID userId) {

        UserStatistics statistics = getStatistics(userId);

        statistics.setFollowing(
                statistics.getFollowing() + 1
        );

        UserStatistics savedStatistics =
                statisticsRepository.save(statistics);

        return convertToResponse(savedStatistics);
    }

    private UserStatistics getStatistics(UUID userId) {

        return statisticsRepository.findByUserUserId(userId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Statistics not found for user: "
                                        + userId
                        )
                );
    }

    private UserStatisticsResponse convertToResponse(
            UserStatistics statistics) {

        return new UserStatisticsResponse(
                statistics.getUser().getUserId(),
                statistics.getTotalProjects(),
                statistics.getTotalSnippets(),
                statistics.getTotalViews(),
                statistics.getTotalLikes(),
                statistics.getTotalFavorites(),
                statistics.getFollowers(),
                statistics.getFollowing()
        );
    }
}