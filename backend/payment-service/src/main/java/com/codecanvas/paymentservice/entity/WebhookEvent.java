package com.codecanvas.paymentservice.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import com.codecanvas.paymentservice.enums.WebhookProcessingStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "payment_webhook_events")
public class WebhookEvent {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID webhookEventId;

    @Column(nullable = false, unique = true)
    private String razorpayEventId;

    @Column(nullable = false)
    private String eventType;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String payload;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WebhookProcessingStatus status;

    @Column(length = 1000)
    private String failureReason;

    @Column(nullable = false)
    private LocalDateTime receivedAt;

    private LocalDateTime processedAt;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public WebhookEvent() {
    }

    @PrePersist
    public void prePersist() {

        if (webhookEventId == null) {
            webhookEventId = UUID.randomUUID();
        }

        LocalDateTime now = LocalDateTime.now();

        createdAt = now;
        updatedAt = now;

        if (receivedAt == null) {
            receivedAt = now;
        }

        if (status == null) {
            status = WebhookProcessingStatus.RECEIVED;
        }

    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public UUID getWebhookEventId() {
        return webhookEventId;
    }

    public void setWebhookEventId(UUID webhookEventId) {
        this.webhookEventId = webhookEventId;
    }

    public String getRazorpayEventId() {
        return razorpayEventId;
    }

    public void setRazorpayEventId(String razorpayEventId) {
        this.razorpayEventId = razorpayEventId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public WebhookProcessingStatus getStatus() {
        return status;
    }

    public void setStatus(WebhookProcessingStatus status) {
        this.status = status;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public LocalDateTime getReceivedAt() {
        return receivedAt;
    }

    public void setReceivedAt(LocalDateTime receivedAt) {
        this.receivedAt = receivedAt;
    }

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(LocalDateTime processedAt) {
        this.processedAt = processedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

}