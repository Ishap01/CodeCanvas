package com.codecanvas.userservice.dto.subscription;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsageStatusResponse {
    private String metricName;
    private Boolean isLimitExceeded;
    private Integer remainingUsage;
}
