package com.codecanvas.paymentservice.client.dto;

import java.util.UUID;

public class ActivatePremiumRequest {

    private UUID userId;

    private UUID paymentId;

    private UUID subscriptionPlanId;

    public ActivatePremiumRequest() {
    }

    public ActivatePremiumRequest(
            UUID userId,
            UUID paymentId,
            UUID subscriptionPlanId) {

        this.userId = userId;
        this.paymentId = paymentId;
        this.subscriptionPlanId = subscriptionPlanId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(UUID paymentId) {
        this.paymentId = paymentId;
    }

    public UUID getSubscriptionPlanId() {
        return subscriptionPlanId;
    }

    public void setSubscriptionPlanId(
            UUID subscriptionPlanId) {

        this.subscriptionPlanId = subscriptionPlanId;
    }
}