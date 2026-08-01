package com.codecanvas.userservice.dto.subscription;

import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateSubscriptionRequest {
    private UUID userId;
    private Long planId;
    private String paymentId;
    private String paymentMethod;
}
