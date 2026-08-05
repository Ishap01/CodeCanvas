package com.codecanvas.userservice.repository;

import com.codecanvas.userservice.entity.UserSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserSubscriptionRepository
        extends JpaRepository<UserSubscription, Long> {

    Optional<UserSubscription> findByUserIdAndStatusIn(
            UUID userId,
            List<UserSubscription.SubscriptionStatus> statuses
    );

    @Query("""
            SELECT us
            FROM UserSubscription us
            WHERE us.userId = :userId
              AND us.status = 'ACTIVE'
            """)
    Optional<UserSubscription> findActiveSubscriptionByUserId(
            @Param("userId") UUID userId
    );

    List<UserSubscription> findByRenewalDateBetweenAndAutoRenewTrue(
            LocalDateTime startDate,
            LocalDateTime endDate
    );

    List<UserSubscription> findByUserIdOrderByCreatedAtDesc(
            UUID userId
    );

    @Query("""
            SELECT COUNT(us)
            FROM UserSubscription us
            WHERE us.plan.id = :planId
              AND us.status = 'ACTIVE'
            """)
    long countActiveSubscriptionsByPlanId(
            @Param("planId") Long planId
    );

    /*
     * Same Razorpay payment se subscription pehle create hui hai
     * ya nahi, ye check karega.
     */
    boolean existsByPaymentId(String paymentId);

    Optional<UserSubscription> findByPaymentId(String paymentId);
}