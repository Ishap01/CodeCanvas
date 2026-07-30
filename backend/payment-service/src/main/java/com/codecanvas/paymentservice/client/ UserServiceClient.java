package com.codecanvas.paymentservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.codecanvas.paymentservice.client.dto.ActivatePremiumRequest;
import com.codecanvas.paymentservice.client.dto.UserServiceResponse;

@FeignClient(
        name = "USER-SERVICE",
        configuration = com.codecanvas.paymentservice.config.FeignConfig.class
)
public interface UserServiceClient {

    @PatchMapping("/api/users/premium/activate")
    ResponseEntity<UserServiceResponse> activatePremiumMembership(

            @RequestHeader("Authorization")
            String authorization,

            @RequestBody
            ActivatePremiumRequest request
    );
}