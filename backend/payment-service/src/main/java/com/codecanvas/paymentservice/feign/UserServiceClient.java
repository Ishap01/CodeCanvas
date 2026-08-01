package com.codecanvas.paymentservice.feign;

import com.codecanvas.paymentservice.config.FeignConfig;
import com.codecanvas.paymentservice.dto.request.CreateSubscriptionRequest;
import com.codecanvas.paymentservice.dto.response.SubscriptionPlanResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(
        name = "user-service",
        configuration = FeignConfig.class
)
public interface UserServiceClient {

    @GetMapping("/api/subscriptions/plans/{planId}")
    SubscriptionPlanResponse getPlanById(
            @PathVariable("planId") Long planId
    );

    @PostMapping("/api/subscriptions")
    ResponseEntity<Void> createSubscription(

            @RequestHeader("Authorization")
            String authorization,

            @RequestBody
            CreateSubscriptionRequest request
    );
}