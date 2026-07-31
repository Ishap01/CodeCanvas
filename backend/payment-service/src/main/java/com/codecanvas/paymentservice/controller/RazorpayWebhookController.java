package com.codecanvas.paymentservice.controller;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codecanvas.paymentservice.dto.response.ApiResponse;
import com.codecanvas.paymentservice.service.WebhookService;

@RestController
@RequestMapping("/api/payments/webhooks")
public class RazorpayWebhookController {

    private static final String RAZORPAY_SIGNATURE_HEADER =
            "X-Razorpay-Signature";

    private static final String RAZORPAY_EVENT_ID_HEADER =
            "X-Razorpay-Event-Id";

    private final WebhookService webhookService;

    public RazorpayWebhookController(
            WebhookService webhookService) {

        this.webhookService = webhookService;
    }

    @PostMapping("/razorpay")
    public ResponseEntity<ApiResponse<Void>> processRazorpayWebhook(
            @RequestBody String rawPayload,
            @RequestHeader(RAZORPAY_SIGNATURE_HEADER)
            String webhookSignature,
            @RequestHeader(RAZORPAY_EVENT_ID_HEADER)
            String razorpayEventId) {

        webhookService.processRazorpayWebhook(
                rawPayload,
                webhookSignature,
                razorpayEventId
        );

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Razorpay webhook processed successfully.")
                        .data(null)
                        .build()
        );
    }
}
