package com.codecanvas.snippetservice.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceClient {

    private final RestTemplate restTemplate;

    private static final String USER_SERVICE_URL =
            "http://user-service/api/subscriptions/internal/premium/{userId}";

    public boolean isPremiumUser(UUID userId) {

        Boolean response =
                restTemplate.getForObject(
                        USER_SERVICE_URL,
                        Boolean.class,
                        userId
                );

        return Boolean.TRUE.equals(response);
    }
}