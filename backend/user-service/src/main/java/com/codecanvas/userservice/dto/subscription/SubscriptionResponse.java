package com.codecanvas.userservice.dto.subscription;

import com.codecanvas.userservice.entity.UserSubscription;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionResponse {
    private Long subscriptionId;
    private UUID userId;
    private SubscriptionPlanDTO plan;
    private String status;
    private LocalDateTime startedAt;
    private LocalDateTime endsAt;
    private LocalDateTime renewalDate;
    private Boolean autoRenew;
    private Boolean isActive;

    public static SubscriptionResponse freeUser() {
        return SubscriptionResponse.builder()
                .status("FREE")
                .isActive(true)
                .build();
    }

    public SubscriptionResponse(UserSubscription subscription) {
        if (subscription != null) {
            this.subscriptionId = subscription.getId();
            this.userId = subscription.getUserId();
            this.plan = SubscriptionPlanDTO.from(subscription.getPlan());
            this.status = subscription.getStatus() != null ? subscription.getStatus().name() : null;
            this.startedAt = subscription.getStartedAt();
            this.endsAt = subscription.getEndsAt();
            this.renewalDate = subscription.getRenewalDate();
            this.autoRenew = subscription.getAutoRenew();
            this.isActive = subscription.isActive();
        }
    }
}
