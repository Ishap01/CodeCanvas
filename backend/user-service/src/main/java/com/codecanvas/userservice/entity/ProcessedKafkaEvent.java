package com.codecanvas.userservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "processed_kafka_events")
public class ProcessedKafkaEvent {

    @Id
    @Column(
            name = "event_id",
            nullable = false,
            updatable = false
    )
    private UUID eventId;

    @Column(
            name = "event_type",
            nullable = false,
            length = 100
    )
    private String eventType;

    @Column(
            name = "payment_id",
            nullable = false
    )
    private UUID paymentId;

    @Column(
            name = "processed_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime processedAt;

    public ProcessedKafkaEvent() {
    }

    public ProcessedKafkaEvent(
            UUID eventId,
            String eventType,
            UUID paymentId,
            LocalDateTime processedAt) {

        this.eventId = eventId;
        this.eventType = eventType;
        this.paymentId = paymentId;
        this.processedAt = processedAt;
    }

    @PrePersist
    public void onCreate() {

        if (this.processedAt == null) {
            this.processedAt = LocalDateTime.now();
        }
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

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(LocalDateTime processedAt) {
        this.processedAt = processedAt;
    }
}