
package com.codecanvas.notificationservice.client;
import com.codecanvas.notificationservice.dto.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;
@Component
@RequiredArgsConstructor
public class UserServiceClient {

    private final RestTemplate restTemplate;

    @Value("${services.user-service.url}")
    private String userServiceUrl;

    public UserResponse getUserById(UUID userId) {
        return restTemplate.getForObject(
                userServiceUrl + "/api/users/" + userId,
                UserResponse.class
        );
    }
}