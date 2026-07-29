package com.codecanvas.userservice.service;

import com.codecanvas.userservice.dto.subscription.SubscriptionPlanDTO;
import com.codecanvas.userservice.entity.SubscriptionHistory;
import com.codecanvas.userservice.entity.SubscriptionPlan;
import com.codecanvas.userservice.entity.UserSubscription;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SubscriptionService {

    UserSubscription createSubscription(UUID userId, Long planId, String paymentId, String paymentMethod);

    UserSubscription renewSubscription(Long subscriptionId, String paymentId);

    UserSubscription upgradePlan(Long subscriptionId, Long newPlanId);

    UserSubscription cancelSubscription(Long subscriptionId, String cancellationReason);

    Optional<UserSubscription> getActiveSubscription(UUID userId);

    boolean isPremiumUser(UUID userId);

    String getUserSubscriptionTier(UUID userId);

    List<SubscriptionPlan> getAllActivePlans();

    Optional<SubscriptionPlan> getPlanByTier(String tier);

    SubscriptionPlan createPlan(SubscriptionPlanDTO dto, String createdBy);

    void trackUsage(UUID userId, String metricName);

    boolean isUsageLimitExceeded(UUID userId, String metricName);

    int getRemainingUsage(UUID userId, String metricName);

    List<SubscriptionHistory> getSubscriptionHistory(UUID userId);

    UUID getCurrentAuthenticatedUserId();
}
