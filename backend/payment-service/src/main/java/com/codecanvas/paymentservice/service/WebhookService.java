package com.codecanvas.paymentservice.service;

public interface WebhookService {

    void processRazorpayWebhook(
            String payload,
            String webhookSignature,
            String razorpayEventId
    );
}