package com.codecanvas.userservice.dto.subscription;

import com.codecanvas.userservice.entity.SubscriptionPlan;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionPlanDTO {
    private Long id;
    private String name;
    private String description;
    private String tier;
    private BigDecimal price;
    private String currency;
    private Integer billingCycleDays;
    private Integer maxSnippetsPerMonth;
    private Integer aiRequestsPerMonth;
    private Boolean prioritySupport;
    private String customBadge;

    public static SubscriptionPlanDTO from(SubscriptionPlan plan) {
        if (plan == null) return null;
        return SubscriptionPlanDTO.builder()
                .id(plan.getId())
                .name(plan.getName())
                .description(plan.getDescription())
                .tier(plan.getTier())
                .price(plan.getPrice())
                .currency(plan.getCurrency())
                .billingCycleDays(plan.getBillingCycleDays())
                .maxSnippetsPerMonth(plan.getMaxSnippetsPerMonth())
                .aiRequestsPerMonth(plan.getAiRequestsPerMonth())
                .prioritySupport(plan.getPrioritySupport())
                .customBadge(plan.getCustomBadge())
                .build();
    }
}
