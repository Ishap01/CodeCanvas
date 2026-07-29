package com.codecanvas.userservice.service.impl;

import com.codecanvas.userservice.dto.subscription.SubscriptionPlanDTO;
import com.codecanvas.userservice.entity.*;
import com.codecanvas.userservice.repository.*;
import com.codecanvas.userservice.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SubscriptionServiceImpl implements SubscriptionService {

    private final UserSubscriptionRepository subscriptionRepository;
    private final SubscriptionPlanRepository planRepository;
    private final SubscriptionHistoryRepository historyRepository;
    private final UsageTrackingRepository usageRepository;
    private final UserRepository userRepository;

    @Override
    public UserSubscription createSubscription(
            UUID userId,
            Long planId,
            String paymentId,
            String paymentMethod) {

        SubscriptionPlan plan = planRepository.findById(planId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Subscription plan not found: " + planId));

        LocalDateTime now = LocalDateTime.now();

        // Deactivate existing active subscriptions if any
        subscriptionRepository.findActiveSubscriptionByUserId(userId).ifPresent(existing -> {
            existing.setStatus(UserSubscription.SubscriptionStatus.CANCELLED);
            existing.setCancelledAt(now);
            existing.setCancellationReason("Replaced by new subscription");
            subscriptionRepository.save(existing);
        });

        UserSubscription subscription = UserSubscription.builder()
                .userId(userId)
                .plan(plan)
                .status(UserSubscription.SubscriptionStatus.ACTIVE)
                .startedAt(now)
                .endsAt(now.plusDays(plan.getBillingCycleDays()))
                .renewalDate(now.plusDays(plan.getBillingCycleDays()))
                .paymentId(paymentId)
                .paymentMethod(paymentMethod)
                .autoRenew(true)
                .build();

        UserSubscription saved = subscriptionRepository.save(subscription);

        recordSubscriptionEvent(
                userId,
                saved.getId(),
                SubscriptionHistory.SubscriptionEventType.SUBSCRIPTION_CREATED,
                null,
                planId,
                "Subscription created via payment (" + paymentMethod + ")",
                paymentId
        );

        log.info("Subscription created successfully for user {} with plan {}", userId, planId);
        return saved;
    }

    @Override
    public UserSubscription renewSubscription(Long subscriptionId, String paymentId) {
        UserSubscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Subscription not found: " + subscriptionId));

        LocalDateTime newEndDate = (subscription.getEndsAt() != null ? subscription.getEndsAt() : LocalDateTime.now())
                .plusDays(subscription.getPlan().getBillingCycleDays());

        subscription.setStatus(UserSubscription.SubscriptionStatus.ACTIVE);
        subscription.setEndsAt(newEndDate);
        subscription.setRenewalDate(newEndDate);
        subscription.setPaymentId(paymentId);
        subscription.setUpdatedAt(LocalDateTime.now());

        UserSubscription updated = subscriptionRepository.save(subscription);

        recordSubscriptionEvent(
                subscription.getUserId(),
                subscriptionId,
                SubscriptionHistory.SubscriptionEventType.SUBSCRIPTION_RENEWED,
                null,
                subscription.getPlan().getId(),
                "Subscription renewed until " + newEndDate,
                paymentId
        );

        log.info("Subscription {} renewed for user {}", subscriptionId, subscription.getUserId());
        return updated;
    }

    @Override
    public UserSubscription upgradePlan(Long subscriptionId, Long newPlanId) {
        UserSubscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Subscription not found: " + subscriptionId));

        SubscriptionPlan newPlan = planRepository.findById(newPlanId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Plan not found: " + newPlanId));

        Long oldPlanId = subscription.getPlan().getId();
        subscription.setPlan(newPlan);
        subscription.setUpdatedAt(LocalDateTime.now());

        UserSubscription updated = subscriptionRepository.save(subscription);

        recordSubscriptionEvent(
                subscription.getUserId(),
                subscriptionId,
                SubscriptionHistory.SubscriptionEventType.PLAN_UPGRADED,
                oldPlanId,
                newPlanId,
                "Plan upgraded to " + newPlan.getName(),
                null
        );

        log.info("Subscription {} upgraded from plan {} to {}", subscriptionId, oldPlanId, newPlanId);
        return updated;
    }

    @Override
    public UserSubscription cancelSubscription(Long subscriptionId, String cancellationReason) {
        UserSubscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Subscription not found: " + subscriptionId));

        subscription.setStatus(UserSubscription.SubscriptionStatus.CANCELLED);
        subscription.setCancelledAt(LocalDateTime.now());
        subscription.setCancellationReason(cancellationReason);
        subscription.setAutoRenew(false);
        subscription.setUpdatedAt(LocalDateTime.now());

        UserSubscription updated = subscriptionRepository.save(subscription);

        recordSubscriptionEvent(
                subscription.getUserId(),
                subscriptionId,
                SubscriptionHistory.SubscriptionEventType.SUBSCRIPTION_CANCELLED,
                null,
                null,
                cancellationReason != null ? cancellationReason : "User requested cancellation",
                null
        );

        log.info("Subscription {} cancelled: {}", subscriptionId, cancellationReason);
        return updated;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserSubscription> getActiveSubscription(UUID userId) {
        return subscriptionRepository.findActiveSubscriptionByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isPremiumUser(UUID userId) {
        return getActiveSubscription(userId)
                .map(sub -> sub.getPlan() != null && 
                        ("BASIC_PREMIUM".equalsIgnoreCase(sub.getPlan().getTier()) || 
                         "PRO_PREMIUM".equalsIgnoreCase(sub.getPlan().getTier())))
                .orElse(false);
    }

    @Override
    @Transactional(readOnly = true)
    public String getUserSubscriptionTier(UUID userId) {
        return getActiveSubscription(userId)
                .map(sub -> sub.getPlan() != null ? sub.getPlan().getTier() : "FREE")
                .orElse("FREE");
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubscriptionPlan> getAllActivePlans() {
        return planRepository.findAllByIsActiveTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SubscriptionPlan> getPlanByTier(String tier) {
        return planRepository.findByTier(tier);
    }

    @Override
    public SubscriptionPlan createPlan(SubscriptionPlanDTO dto, String createdBy) {
        SubscriptionPlan plan = SubscriptionPlan.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .tier(dto.getTier())
                .price(dto.getPrice())
                .currency(dto.getCurrency() != null ? dto.getCurrency() : "INR")
                .billingCycleDays(dto.getBillingCycleDays() != null ? dto.getBillingCycleDays() : 30)
                .maxSnippetsPerMonth(dto.getMaxSnippetsPerMonth())
                .aiRequestsPerMonth(dto.getAiRequestsPerMonth())
                .prioritySupport(dto.getPrioritySupport() != null ? dto.getPrioritySupport() : false)
                .customBadge(dto.getCustomBadge())
                .isActive(true)
                .createdBy(createdBy)
                .build();

        return planRepository.save(plan);
    }

    @Override
    public void trackUsage(UUID userId, String metricName) {
        UsageTracking usage = usageRepository.findByUserIdAndMetricName(userId, metricName)
                .orElseGet(() -> {
                    UsageTracking newUsage = UsageTracking.builder()
                            .userId(userId)
                            .metricName(metricName)
                            .currentCount(0)
                            .resetDate(LocalDateTime.now().plusMonths(1))
                            .build();
                    return usageRepository.save(newUsage);
                });

        if (usage.shouldReset()) {
            usage.setCurrentCount(0);
            usage.setResetDate(LocalDateTime.now().plusMonths(1));
        }

        usage.setCurrentCount(usage.getCurrentCount() + 1);
        usageRepository.save(usage);
    }

    @Override
    public boolean isUsageLimitExceeded(UUID userId, String metricName) {
        UsageTracking usage = usageRepository.findByUserIdAndMetricName(userId, metricName)
                .orElse(null);

        if (usage == null) return false;

        if (usage.shouldReset()) {
            usage.setCurrentCount(0);
            usage.setResetDate(LocalDateTime.now().plusMonths(1));
            usageRepository.save(usage);
        }

        return usage.isLimitExceeded();
    }

    @Override
    public int getRemainingUsage(UUID userId, String metricName) {
        UsageTracking usage = usageRepository.findByUserIdAndMetricName(userId, metricName)
                .orElse(null);

        if (usage == null) return -1;

        if (usage.shouldReset()) {
            usage.setCurrentCount(0);
            usage.setResetDate(LocalDateTime.now().plusMonths(1));
            usageRepository.save(usage);
        }

        return usage.getRemainingUsage();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubscriptionHistory> getSubscriptionHistory(UUID userId) {
        return historyRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public UUID getCurrentAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not authenticated");
        }

        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Authenticated user not found: " + email));

        return user.getUserId();
    }

    private void recordSubscriptionEvent(
            UUID userId,
            Long subscriptionId,
            SubscriptionHistory.SubscriptionEventType eventType,
            Long previousPlanId,
            Long newPlanId,
            String description,
            String transactionId) {

        SubscriptionHistory history = SubscriptionHistory.builder()
                .userId(userId)
                .subscriptionId(subscriptionId)
                .eventType(eventType)
                .previousPlanId(previousPlanId)
                .newPlanId(newPlanId)
                .description(description)
                .transactionId(transactionId)
                .build();

        historyRepository.save(history);
    }
}
