package com.codecanvas.paymentservice.service;

import java.util.UUID;

import com.codecanvas.paymentservice.entity.Payment;

public interface SubscriptionActivationService {

    boolean activatePremiumSubscription(
            UUID userId,
            Payment payment
    );

}