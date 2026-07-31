package com.codecanvas.paymentservice.feign;

import com.codecanvas.paymentservice.config.FeignConfig;
import com.codecanvas.paymentservice.dto.response.SubscriptionPlanResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "user-service",
        configuration = FeignConfig.class
)
public interface UserServiceClient {

    @GetMapping("/api/subscriptions/plans/{planId}")
    SubscriptionPlanResponse getPlanById(
            @PathVariable Long planId
    );

}