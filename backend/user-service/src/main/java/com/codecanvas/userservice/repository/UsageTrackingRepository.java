package com.codecanvas.userservice.repository;

import com.codecanvas.userservice.entity.UsageTracking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsageTrackingRepository extends JpaRepository<UsageTracking, Long> {

    Optional<UsageTracking> findByUserIdAndMetricName(UUID userId, String metricName);

    List<UsageTracking> findByUserId(UUID userId);

    @Query("SELECT ut FROM UsageTracking ut WHERE ut.userId = :userId AND ut.resetDate < CURRENT_TIMESTAMP")
    List<UsageTracking> findExpiredMetricsForUser(@Param("userId") UUID userId);
}
