package com.codecanvas.paymentservice.dto.request;

import java.util.UUID;

public class CreateSubscriptionRequest {

    private UUID userId;
    private Long planId;
    private String paymentId;
    private String paymentMethod;

    public CreateSubscriptionRequest() {
    }

    public CreateSubscriptionRequest(
            UUID userId,
            Long planId,
            String paymentId,
            String paymentMethod) {

        this.userId = userId;
        this.planId = planId;
        this.paymentId = paymentId;
        this.paymentMethod = paymentMethod;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}