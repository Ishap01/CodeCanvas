package com.codecanvas.paymentservice.service.impl;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.codecanvas.paymentservice.client.UserServiceClient;
import com.codecanvas.paymentservice.client.dto.ActivatePremiumRequest;
import com.codecanvas.paymentservice.client.dto.UserServiceResponse;
import com.codecanvas.paymentservice.entity.Payment;
import com.codecanvas.paymentservice.service.SubscriptionActivationService;

import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class SubscriptionActivationServiceImpl
        implements SubscriptionActivationService {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(
                    SubscriptionActivationServiceImpl.class
            );

    private final UserServiceClient userServiceClient;
    private final HttpServletRequest httpServletRequest;

    public SubscriptionActivationServiceImpl(
            UserServiceClient userServiceClient,
            HttpServletRequest httpServletRequest) {

        this.userServiceClient = userServiceClient;
        this.httpServletRequest = httpServletRequest;
    }

    @Override
    public boolean activatePremiumSubscription(
            UUID userId,
            Payment payment) {

        validateActivationRequest(
                userId,
                payment
        );

        String authorizationHeader =
                httpServletRequest.getHeader(
                        HttpHeaders.AUTHORIZATION
                );

        if (authorizationHeader == null
                || authorizationHeader.isBlank()) {

            LOGGER.error(
                    "Premium activation failed because Authorization header "
                            + "is missing. userId={}, paymentId={}",
                    userId,
                    payment.getPaymentId()
            );

            return false;
        }

        ActivatePremiumRequest request =
                new ActivatePremiumRequest(
                        userId,
                        payment.getPaymentId(),
                        payment.getSubscriptionPlanId()
                );

        try {
            ResponseEntity<UserServiceResponse> response =
                    userServiceClient
                            .activatePremiumMembership(
                                    authorizationHeader,
                                    request
                            );

            UserServiceResponse responseBody =
                    response.getBody();

            boolean activated =
                    response.getStatusCode()
                            .is2xxSuccessful()
                            && responseBody != null
                            && responseBody.isSuccess();

            if (activated) {

                LOGGER.info(
                        "Premium subscription activated. "
                                + "userId={}, paymentId={}, planId={}",
                        userId,
                        payment.getPaymentId(),
                        payment.getSubscriptionPlanId()
                );

            } else {

                LOGGER.error(
                        "User Service rejected premium activation. "
                                + "userId={}, paymentId={}, status={}",
                        userId,
                        payment.getPaymentId(),
                        response.getStatusCode()
                );
            }

            return activated;

        } catch (FeignException exception) {

            LOGGER.error(
                    "User Service communication failed during premium "
                            + "activation. userId={}, paymentId={}, status={}",
                    userId,
                    payment.getPaymentId(),
                    exception.status(),
                    exception
            );

            return false;

        } catch (Exception exception) {

            LOGGER.error(
                    "Unexpected error during premium activation. "
                            + "userId={}, paymentId={}",
                    userId,
                    payment.getPaymentId(),
                    exception
            );

            return false;
        }
    }

    private void validateActivationRequest(
            UUID userId,
            Payment payment) {

        if (userId == null) {
            throw new IllegalArgumentException(
                    "User ID is required for premium activation"
            );
        }

        if (payment == null) {
            throw new IllegalArgumentException(
                    "Payment is required for premium activation"
            );
        }

        if (payment.getPaymentId() == null) {
            throw new IllegalArgumentException(
                    "Payment ID is required for premium activation"
            );
        }

        if (payment.getSubscriptionPlanId() == null) {
            throw new IllegalArgumentException(
                    "Subscription plan ID is required for premium activation"
            );
        }
    }
}