package com.codecanvas.userservice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "subscription_history", indexes = {
    @Index(name = "idx_sub_hist_user", columnList = "user_id"),
    @Index(name = "idx_sub_hist_type", columnList = "event_type"),
    @Index(name = "idx_sub_hist_created", columnList = "created_at DESC")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "subscription_id", nullable = false)
    private Long subscriptionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    private SubscriptionEventType eventType;

    @Column(name = "previous_plan_id")
    private Long previousPlanId;

    @Column(name = "new_plan_id")
    private Long newPlanId;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "transaction_id")
    private String transactionId;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public enum SubscriptionEventType {
        SUBSCRIPTION_CREATED,
        SUBSCRIPTION_RENEWED,
        PLAN_UPGRADED,
        PLAN_DOWNGRADED,
        SUBSCRIPTION_CANCELLED,
        SUBSCRIPTION_SUSPENDED,
        PAYMENT_FAILED,
        AUTO_RENEWAL_DISABLED
    }
}
