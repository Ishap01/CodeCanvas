package com.codecanvas.userservice.repository;

import java.util.Optional;
import java.util.UUID;

import com.codecanvas.userservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import com.codecanvas.userservice.entity.UserStatistics;

public interface UserStatisticsRepository
        extends JpaRepository<UserStatistics, UUID> {

    Optional<UserStatistics> findByUserUserId(UUID userId);

    Optional<UserStatistics> findByUser(User user);
}