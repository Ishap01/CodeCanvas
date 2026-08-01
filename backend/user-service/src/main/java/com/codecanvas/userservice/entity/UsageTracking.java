package com.codecanvas.userservice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "usage_tracking", indexes = {
    @Index(name = "idx_usage_user_metric", columnList = "user_id, metric_name")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsageTracking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "subscription_id")
    private Long subscriptionId;

    @Column(name = "metric_name", nullable = false)
    private String metricName;

    @Builder.Default
    @Column(name = "current_count", columnDefinition = "integer default 0")
    private Integer currentCount = 0;

    @Column(name = "limit_count")
    private Integer limitCount;

    @Column(name = "reset_date")
    private LocalDateTime resetDate;

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

    public boolean isLimitExceeded() {
        return limitCount != null && currentCount >= limitCount;
    }

    public int getRemainingUsage() {
        if (limitCount == null) return Integer.MAX_VALUE;
        return Math.max(0, limitCount - currentCount);
    }

    public boolean shouldReset() {
        return resetDate != null && LocalDateTime.now().isAfter(resetDate);
    }
}
