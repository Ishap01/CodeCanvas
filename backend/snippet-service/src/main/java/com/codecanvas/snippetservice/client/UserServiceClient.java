//package com.codecanvas.snippetservice.client;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//
//import java.util.UUID;
//
//@Service
//@RequiredArgsConstructor
//public class UserServiceClient {
//
//    private final RestTemplate restTemplate;
//
//    private static final String USER_SERVICE_URL =
//            "http://user-service/api/subscriptions/internal/premium/{userId}";
//
//    public boolean isPremiumUser(UUID userId) {


//
//        Boolean response =
//                restTemplate.getForObject(
//                        USER_SERVICE_URL,
//                        Boolean.class,
//                        userId
//                );
//
//        return Boolean.TRUE.equals(response);
//    }
//}


package com.codecanvas.snippetservice.client;

import java.util.UUID;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class UserServiceClient {

    private static final String USER_SERVICE_URL =
            "http://user-service/api/subscriptions/status/{userId}";

    private final RestTemplate restTemplate;

    public UserServiceClient(
            RestTemplate restTemplate) {

        this.restTemplate = restTemplate;
    }

    public boolean isPremiumUser(
            UUID userId) {

        if (userId == null) {
            return false;
        }

        HttpHeaders headers =
                new HttpHeaders();

        String authorizationHeader =
                getCurrentAuthorizationHeader();

        /*
         * Frontend se Snippet Service ko jo JWT mila,
         * wahi User Service ko forward hoga.
         */
        if (authorizationHeader != null
                && !authorizationHeader.isBlank()) {

            headers.set(
                    HttpHeaders.AUTHORIZATION,
                    authorizationHeader
            );
        }

        HttpEntity<Void> requestEntity =
                new HttpEntity<>(headers);

        ResponseEntity<SubscriptionStatusResponse>
                responseEntity =
                restTemplate.exchange(
                        USER_SERVICE_URL,
                        HttpMethod.GET,
                        requestEntity,
                        SubscriptionStatusResponse.class,
                        userId
                );

        SubscriptionStatusResponse response =
                responseEntity.getBody();

        return response != null
                && response.isPremium();
    }

    private String getCurrentAuthorizationHeader() {

        RequestAttributes requestAttributes =
                RequestContextHolder
                        .getRequestAttributes();

        if (!(requestAttributes
                instanceof ServletRequestAttributes)) {

            return null;
        }

        ServletRequestAttributes servletAttributes =
                (ServletRequestAttributes)
                        requestAttributes;

        HttpServletRequest request =
                servletAttributes.getRequest();

        return request.getHeader(
                HttpHeaders.AUTHORIZATION
        );
    }

    /*
     * User Service response:
     *
     * {
     *   "tier": "BASIC_PREMIUM",
     *   "isPremium": true
     * }
     */
    public static class SubscriptionStatusResponse {

        private String tier;

        private boolean isPremium;

        public SubscriptionStatusResponse() {
        }

        public SubscriptionStatusResponse(
                String tier,
                boolean isPremium) {

            this.tier = tier;
            this.isPremium = isPremium;
        }

        public String getTier() {
            return tier;
        }

        public void setTier(
                String tier) {

            this.tier = tier;
        }

        public boolean isPremium() {
            return isPremium;
        }

        public void setPremium(
                boolean premium) {

            isPremium = premium;
        }
    }
}