package com.codecanvas.userservice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_subscriptions", indexes = {
    @Index(name = "idx_user_sub_user", columnList = "user_id"),
    @Index(name = "idx_user_sub_status", columnList = "subscription_status"),
    @Index(name = "idx_user_sub_renewal", columnList = "renewal_date")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSubscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "plan_id", nullable = false)
    private SubscriptionPlan plan;

    @Enumerated(EnumType.STRING)
    @Column(name = "subscription_status", nullable = false)
    private SubscriptionStatus status;

    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;

    @Column(name = "ends_at")
    private LocalDateTime endsAt;

    @Column(name = "renewal_date")
    private LocalDateTime renewalDate;

    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(name = "payment_id", unique = true)
    private String paymentId;

    @Builder.Default
    @Column(name = "is_auto_renew", columnDefinition = "boolean default true")
    private Boolean autoRenew = true;

    @Column(name = "cancellation_reason", columnDefinition = "TEXT")
    private String cancellationReason;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public boolean isActive() {
        LocalDateTime now = LocalDateTime.now();
        return status == SubscriptionStatus.ACTIVE 
            && (endsAt == null || endsAt.isAfter(now));
    }

    public boolean isExpiringSoon(int daysThreshold) {
        if (endsAt == null) return false;
        LocalDateTime warningDate = LocalDateTime.now().plusDays(daysThreshold);
        return endsAt.isBefore(warningDate) && endsAt.isAfter(LocalDateTime.now());
    }

    public enum SubscriptionStatus {
        ACTIVE, CANCELLED, EXPIRED, PENDING, SUSPENDED
    }
}
