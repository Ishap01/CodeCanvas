package com.codecanvas.userservice.dto.subscription;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanUpgradeRequest {
    private Long newPlanId;
}
