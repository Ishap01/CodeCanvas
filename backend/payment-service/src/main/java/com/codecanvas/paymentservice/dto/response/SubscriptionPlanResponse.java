package com.codecanvas.paymentservice.dto.response;

import com.codecanvas.paymentservice.enums.Currency;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class SubscriptionPlanResponse {

    private Long id;

    private String name;

    private String description;

    private String tier;

    private BigDecimal price;

    private Currency currency;

    private Integer billingCycleDays;

    private Integer maxSnippetsPerMonth;

    private Integer aiRequestsPerMonth;

    private Boolean prioritySupport;

    private String customBadge;

}