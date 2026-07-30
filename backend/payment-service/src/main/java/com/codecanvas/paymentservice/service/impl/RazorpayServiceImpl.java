package com.codecanvas.paymentservice.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.codecanvas.paymentservice.config.RazorpayConfig.RazorpayProperties;
import com.codecanvas.paymentservice.dto.request.CreateRefundRequest;
import com.codecanvas.paymentservice.service.RazorpayService;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Refund;
import com.razorpay.Utils;

@Service
public class RazorpayServiceImpl
        implements RazorpayService {

    private final RazorpayClient razorpayClient;
    private final RazorpayProperties razorpayProperties;

    public RazorpayServiceImpl(
            RazorpayClient razorpayClient,
            RazorpayProperties razorpayProperties) {

        this.razorpayClient = razorpayClient;
        this.razorpayProperties = razorpayProperties;
    }

    @Override
    public Order createOrder(
            String receipt,
            long amountInPaise)
            throws RazorpayException {

        if (receipt == null || receipt.isBlank()) {
            throw new IllegalArgumentException(
                    "Receipt is required"
            );
        }

        if (amountInPaise <= 0) {
            throw new IllegalArgumentException(
                    "Payment amount must be greater than zero"
            );
        }

        JSONObject orderRequest =
                new JSONObject();

        orderRequest.put(
                "amount",
                amountInPaise
        );

        orderRequest.put(
                "currency",
                "INR"
        );

        orderRequest.put(
                "receipt",
                receipt
        );

        return razorpayClient
                .orders
                .create(orderRequest);
    }

    @Override
    public boolean verifyPaymentSignature(
            String razorpayOrderId,
            String razorpayPaymentId,
            String razorpaySignature) {

        validatePaymentSignatureData(
                razorpayOrderId,
                razorpayPaymentId,
                razorpaySignature
        );

        JSONObject signatureAttributes =
                new JSONObject();

        signatureAttributes.put(
                "razorpay_order_id",
                razorpayOrderId
        );

        signatureAttributes.put(
                "razorpay_payment_id",
                razorpayPaymentId
        );

        signatureAttributes.put(
                "razorpay_signature",
                razorpaySignature
        );

        try {
            return Utils.verifyPaymentSignature(
                    signatureAttributes,
                    razorpayProperties.getKeySecret()
            );

        } catch (RazorpayException exception) {
            return false;
        }
    }

    @Override
    public Refund createRefund(
            String razorpayPaymentId,
            CreateRefundRequest request)
            throws RazorpayException {

        if (razorpayPaymentId == null
                || razorpayPaymentId.isBlank()) {

            throw new IllegalArgumentException(
                    "Razorpay payment ID is required"
            );
        }

        if (request == null) {
            throw new IllegalArgumentException(
                    "Refund request is required"
            );
        }

        if (request.getAmount() == null
                || request.getAmount()
                .compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                    "Refund amount must be greater than zero"
            );
        }

        long amountInPaise =
                convertRupeesToPaise(
                        request.getAmount()
                );

        JSONObject refundRequest =
                new JSONObject();

        refundRequest.put(
                "amount",
                amountInPaise
        );

        refundRequest.put(
                "speed",
                "normal"
        );

        if (request.getReason() != null
                && !request.getReason().isBlank()) {

            JSONObject notes =
                    new JSONObject();

            notes.put(
                    "reason",
                    request.getReason()
            );

            refundRequest.put(
                    "notes",
                    notes
            );
        }

        return razorpayClient
                .payments
                .refund(
                        razorpayPaymentId,
                        refundRequest
                );
    }

    @Override
    public boolean verifyWebhookSignature(
            String payload,
            String webhookSignature) {

        if (payload == null
                || payload.isBlank()) {

            return false;
        }

        if (webhookSignature == null
                || webhookSignature.isBlank()) {

            return false;
        }

        try {
            return Utils.verifyWebhookSignature(
                    payload,
                    webhookSignature,
                    razorpayProperties.getWebhookSecret()
            );

        } catch (RazorpayException exception) {
            return false;
        }
    }

    private void validatePaymentSignatureData(
            String razorpayOrderId,
            String razorpayPaymentId,
            String razorpaySignature) {

        if (razorpayOrderId == null
                || razorpayOrderId.isBlank()) {

            throw new IllegalArgumentException(
                    "Razorpay order ID is required"
            );
        }

        if (razorpayPaymentId == null
                || razorpayPaymentId.isBlank()) {

            throw new IllegalArgumentException(
                    "Razorpay payment ID is required"
            );
        }

        if (razorpaySignature == null
                || razorpaySignature.isBlank()) {

            throw new IllegalArgumentException(
                    "Razorpay signature is required"
            );
        }
    }

    private long convertRupeesToPaise(
            BigDecimal amountInRupees) {

        return amountInRupees
                .setScale(
                        2,
                        RoundingMode.UNNECESSARY
                )
                .movePointRight(2)
                .longValueExact();
    }
}