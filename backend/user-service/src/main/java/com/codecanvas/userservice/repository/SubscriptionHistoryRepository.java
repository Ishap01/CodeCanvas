package com.codecanvas.userservice.repository;

import com.codecanvas.userservice.entity.SubscriptionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SubscriptionHistoryRepository extends JpaRepository<SubscriptionHistory, Long> {

    List<SubscriptionHistory> findByUserIdOrderByCreatedAtDesc(UUID userId);

    List<SubscriptionHistory> findBySubscriptionIdOrderByCreatedAtDesc(Long subscriptionId);

    @Query("SELECT sh FROM SubscriptionHistory sh WHERE sh.userId = :userId AND sh.eventType = :eventType ORDER BY sh.createdAt DESC")
    List<SubscriptionHistory> findByUserAndEventType(
        @Param("userId") UUID userId,
        @Param("eventType") SubscriptionHistory.SubscriptionEventType eventType
    );
}
