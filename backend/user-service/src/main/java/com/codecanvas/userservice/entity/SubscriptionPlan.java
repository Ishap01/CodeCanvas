package com.codecanvas.userservice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "subscription_plans")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, unique = true, length = 50)
    private String tier;

    @Column(nullable = false)
    private BigDecimal price;

    @Builder.Default
    @Column(length = 3)
    private String currency = "INR";

    @Column(name = "billing_cycle_days", nullable = false)
    private Integer billingCycleDays;

    @Column(name = "max_snippets_per_month")
    private Integer maxSnippetsPerMonth;

    @Column(name = "ai_requests_per_month")
    private Integer aiRequestsPerMonth;

    @Builder.Default
    @Column(name = "priority_support")
    private Boolean prioritySupport = false;

    @Column(name = "custom_badge")
    private String customBadge;

    @Builder.Default
    @Column(name = "is_active", columnDefinition = "boolean default true")
    private Boolean isActive = true;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_by")
    private String createdBy;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum PlanTier {
        FREE, BASIC_PREMIUM, PRO_PREMIUM
    }
}
