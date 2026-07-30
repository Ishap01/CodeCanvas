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

import com.codecanvas.paymentservice.dto.request.CreateRefundRequest;
import com.codecanvas.paymentservice.dto.response.RefundResponse;
import com.codecanvas.paymentservice.service.RefundService;
import com.codecanvas.paymentservice.util.AuthenticatedUserExtractor;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/refunds")
public class RefundController {

    private final RefundService refundService;
    private final AuthenticatedUserExtractor authenticatedUserExtractor;

    public RefundController(
            RefundService refundService,
            AuthenticatedUserExtractor authenticatedUserExtractor) {

        this.refundService = refundService;
        this.authenticatedUserExtractor =
                authenticatedUserExtractor;
    }

    @PostMapping
    public ResponseEntity<RefundResponse> createRefund(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody
            CreateRefundRequest request) {

        UUID requestedByUserId =
                authenticatedUserExtractor.extractUserId(jwt);

        RefundResponse response =
                refundService.createRefund(
                        requestedByUserId,
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{refundId}")
    public ResponseEntity<RefundResponse> getRefundById(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID refundId) {

        authenticatedUserExtractor.extractUserId(jwt);

        RefundResponse response =
                refundService.getRefundById(refundId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/payment/{paymentId}")
    public ResponseEntity<List<RefundResponse>>
    getRefundsByPaymentId(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID paymentId) {

        authenticatedUserExtractor.extractUserId(jwt);

        List<RefundResponse> responses =
                refundService.getRefundsByPaymentId(
                        paymentId
                );

        return ResponseEntity.ok(responses);
    }

    @GetMapping
    public ResponseEntity<List<RefundResponse>> getAllRefunds(
            @AuthenticationPrincipal Jwt jwt) {

        authenticatedUserExtractor.extractUserId(jwt);

        List<RefundResponse> responses =
                refundService.getAllRefunds();

        return ResponseEntity.ok(responses);
    }
}