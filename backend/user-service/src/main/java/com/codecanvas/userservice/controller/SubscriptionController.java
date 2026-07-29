package com.codecanvas.userservice.controller;

import com.codecanvas.userservice.dto.subscription.*;
import com.codecanvas.userservice.entity.UserSubscription;
import com.codecanvas.userservice.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    /**
     * Get active subscription for the currently authenticated user
     */
    @GetMapping("/me")
    public ResponseEntity<SubscriptionResponse> getMySubscription() {
        UUID currentUserId = subscriptionService.getCurrentAuthenticatedUserId();

        return subscriptionService.getActiveSubscription(currentUserId)
                .map(sub -> ResponseEntity.ok(new SubscriptionResponse(sub)))
                .orElse(ResponseEntity.ok(SubscriptionResponse.freeUser()));
    }

    /**
     * Get subscription status & tier for any user
     */
    @GetMapping("/status/{userId}")
    public ResponseEntity<Map<String, Object>> getSubscriptionStatus(
            @PathVariable UUID userId) {

        return ResponseEntity.ok(Map.of(
                "isPremium", subscriptionService.isPremiumUser(userId),
                "tier", subscriptionService.getUserSubscriptionTier(userId)
        ));
    }

    /**
     * Get all active subscription plans
     */
    @GetMapping("/plans")
    public ResponseEntity<List<SubscriptionPlanDTO>> getAvailablePlans() {
        return ResponseEntity.ok(
                subscriptionService.getAllActivePlans()
                        .stream()
                        .map(SubscriptionPlanDTO::from)
                        .toList()
        );
    }

    /**
     * Create new subscription (e.g. after successful payment)
     */
    @PostMapping
    public ResponseEntity<SubscriptionResponse> createSubscription(
            @RequestBody CreateSubscriptionRequest request) {

        UserSubscription subscription = subscriptionService.createSubscription(
                request.getUserId(),
                request.getPlanId(),
                request.getPaymentId(),
                request.getPaymentMethod()
        );

        return ResponseEntity.ok(new SubscriptionResponse(subscription));
    }

    /**
     * Renew an existing subscription
     */
    @PostMapping("/{subscriptionId}/renew")
    public ResponseEntity<SubscriptionResponse> renewSubscription(
            @PathVariable Long subscriptionId,
            @RequestParam String paymentId) {

        UserSubscription subscription = subscriptionService.renewSubscription(
                subscriptionId,
                paymentId
        );

        return ResponseEntity.ok(new SubscriptionResponse(subscription));
    }

    /**
     * Upgrade subscription plan
     */
    @PostMapping("/{subscriptionId}/upgrade")
    public ResponseEntity<SubscriptionResponse> upgradePlan(
            @PathVariable Long subscriptionId,
            @RequestBody PlanUpgradeRequest request) {

        UserSubscription subscription = subscriptionService.upgradePlan(
                subscriptionId,
                request.getNewPlanId()
        );

        return ResponseEntity.ok(new SubscriptionResponse(subscription));
    }

    /**
     * Cancel active subscription
     */
    @PostMapping("/{subscriptionId}/cancel")
    public ResponseEntity<SubscriptionResponse> cancelSubscription(
            @PathVariable Long subscriptionId,
            @RequestBody CancelSubscriptionRequest request) {

        UserSubscription subscription = subscriptionService.cancelSubscription(
                subscriptionId,
                request.getReason()
        );

        return ResponseEntity.ok(new SubscriptionResponse(subscription));
    }

    /**
     * Get subscription history for the currently authenticated user
     */
    @GetMapping("/history")
    public ResponseEntity<List<SubscriptionHistoryDTO>> getSubscriptionHistory() {
        UUID currentUserId = subscriptionService.getCurrentAuthenticatedUserId();

        return ResponseEntity.ok(
                subscriptionService.getSubscriptionHistory(currentUserId)
                        .stream()
                        .map(SubscriptionHistoryDTO::from)
                        .toList()
        );
    }

    /**
     * Check usage status for a specific feature metric
     */
    @GetMapping("/usage/{metricName}")
    public ResponseEntity<UsageStatusResponse> checkUsageStatus(
            @PathVariable String metricName) {

        UUID currentUserId = subscriptionService.getCurrentAuthenticatedUserId();

        return ResponseEntity.ok(UsageStatusResponse.builder()
                .metricName(metricName)
                .isLimitExceeded(subscriptionService.isUsageLimitExceeded(currentUserId, metricName))
                .remainingUsage(subscriptionService.getRemainingUsage(currentUserId, metricName))
                .build()
        );
    }
}
