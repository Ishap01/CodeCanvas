package com.codecanvas.paymentservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codecanvas.paymentservice.service.WebhookService;

@RestController
@RequestMapping("/api/payments/webhooks")
public class RazorpayWebhookController {

    private final WebhookService webhookService;

    public RazorpayWebhookController(
            WebhookService webhookService) {

        this.webhookService = webhookService;
    }

    @PostMapping("/razorpay")
    public ResponseEntity<Void> handleRazorpayWebhook(
            @RequestBody String payload,

            @RequestHeader("X-Razorpay-Signature")
            String webhookSignature,

            @RequestHeader("X-Razorpay-Event-Id")
            String razorpayEventId) {

        webhookService.processRazorpayWebhook(
                payload,
                webhookSignature,
                razorpayEventId
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }
}