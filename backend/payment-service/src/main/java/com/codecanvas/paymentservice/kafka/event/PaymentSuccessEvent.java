package com.codecanvas.paymentservice.kafka.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class PaymentSuccessEvent {

    private UUID eventId;
    private String eventType;

    private UUID paymentId;
    private UUID userId;

    private Long planId;
    private String planName;

    private String razorpayPaymentId;

    private BigDecimal amount;
    private String currency;

    private LocalDateTime occurredAt;

    public PaymentSuccessEvent() {
    }

    public PaymentSuccessEvent(
            UUID eventId,
            String eventType,
            UUID paymentId,
            UUID userId,
            Long planId,
            String planName,
            String razorpayPaymentId,
            BigDecimal amount,
            String currency,
            LocalDateTime occurredAt) {

        this.eventId = eventId;
        this.eventType = eventType;
        this.paymentId = paymentId;
        this.userId = userId;
        this.planId = planId;
        this.planName = planName;
        this.razorpayPaymentId = razorpayPaymentId;
        this.amount = amount;
        this.currency = currency;
        this.occurredAt = occurredAt;
    }

    public UUID getEventId() {
        return eventId;
    }

    public void setEventId(UUID eventId) {
        this.eventId = eventId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public UUID getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(UUID paymentId) {
        this.paymentId = paymentId;
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

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public String getRazorpayPaymentId() {
        return razorpayPaymentId;
    }

    public void setRazorpayPaymentId(String razorpayPaymentId) {
        this.razorpayPaymentId = razorpayPaymentId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }

    public void setOccurredAt(LocalDateTime occurredAt) {
        this.occurredAt = occurredAt;
    }
}