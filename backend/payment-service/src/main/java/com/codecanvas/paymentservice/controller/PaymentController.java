package com.codecanvas.paymentservice.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codecanvas.paymentservice.dto.request.CreatePaymentOrderRequest;
import com.codecanvas.paymentservice.dto.request.VerifyPaymentRequest;
import com.codecanvas.paymentservice.dto.response.PaymentOrderResponse;
import com.codecanvas.paymentservice.dto.response.PaymentResponse;
import com.codecanvas.paymentservice.dto.response.PaymentVerificationResponse;
import com.codecanvas.paymentservice.service.PaymentService;
import com.codecanvas.paymentservice.util.AuthenticatedUserExtractor;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final AuthenticatedUserExtractor authenticatedUserExtractor;

    public PaymentController(
            PaymentService paymentService,
            AuthenticatedUserExtractor authenticatedUserExtractor) {

        this.paymentService = paymentService;
        this.authenticatedUserExtractor =
                authenticatedUserExtractor;
    }

    @PostMapping("/order")
    public ResponseEntity<PaymentOrderResponse> createPaymentOrder(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody
            CreatePaymentOrderRequest request) {

        UUID userId =
                authenticatedUserExtractor.extractUserId(jwt);

        PaymentOrderResponse response =
                paymentService.createPaymentOrder(
                        userId,
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/verify")
    public ResponseEntity<PaymentVerificationResponse> verifyPayment(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody
            VerifyPaymentRequest request) {

        UUID userId =
                authenticatedUserExtractor.extractUserId(jwt);

        PaymentVerificationResponse response =
                paymentService.verifyPayment(
                        userId,
                        request
                );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentResponse> getPaymentById(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID paymentId) {

        UUID authenticatedUserId =
                authenticatedUserExtractor.extractUserId(jwt);

        PaymentResponse response =
                paymentService.getPaymentById(paymentId);

        if (!authenticatedUserId.equals(
                response.getUserId())) {

            throw new com.codecanvas.paymentservice.exception
                    .UnauthorizedActionException(
                    "You are not allowed to access this payment"
            );
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/my")
    public ResponseEntity<List<PaymentResponse>> getMyPayments(
            @AuthenticationPrincipal Jwt jwt) {

        UUID userId =
                authenticatedUserExtractor.extractUserId(jwt);

        List<PaymentResponse> responses =
                paymentService.getUserPayments(userId);

        return ResponseEntity.ok(responses);
    }
}