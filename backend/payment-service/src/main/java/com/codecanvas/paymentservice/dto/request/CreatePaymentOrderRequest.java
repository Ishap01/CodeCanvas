package com.codecanvas.paymentservice.dto.request;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public class CreatePaymentOrderRequest {

    @NotNull(message = "Subscription plan ID is required")
    private UUID subscriptionPlanId;

    public CreatePaymentOrderRequest() {
    }

    public CreatePaymentOrderRequest(UUID subscriptionPlanId) {
        this.subscriptionPlanId = subscriptionPlanId;
    }

    public UUID getSubscriptionPlanId() {
        return subscriptionPlanId;
    }

    public void setSubscriptionPlanId(UUID subscriptionPlanId) {
        this.subscriptionPlanId = subscriptionPlanId;
    }
}