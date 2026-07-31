package com.codecanvas.paymentservice.service.impl;

import com.codecanvas.paymentservice.service.RazorpayService;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RazorpayServiceImpl implements RazorpayService {

    private final RazorpayClient razorpayClient;

    @Value("${razorpay.key-secret}")
    private String keySecret;

    @Value("${razorpay.webhook-secret:}")
    private String webhookSecret;

    @Override
    public Order createOrder(
            String receipt,
            long amountInPaise)
            throws RazorpayException {

        if (receipt == null || receipt.isBlank()) {
            throw new IllegalArgumentException("Receipt cannot be empty.");
        }

        if (amountInPaise <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero.");
        }

        JSONObject orderRequest = new JSONObject();

        orderRequest.put("amount", amountInPaise);
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", receipt);

        return razorpayClient.orders.create(orderRequest);
    }

    @Override
    public boolean verifyPaymentSignature(
            String razorpayOrderId,
            String razorpayPaymentId,
            String razorpaySignature) {

        if (razorpayOrderId == null || razorpayOrderId.isBlank()) {
            return false;
        }

        if (razorpayPaymentId == null || razorpayPaymentId.isBlank()) {
            return false;
        }

        if (razorpaySignature == null || razorpaySignature.isBlank()) {
            return false;
        }

        JSONObject attributes = new JSONObject();

        attributes.put("razorpay_order_id", razorpayOrderId);
        attributes.put("razorpay_payment_id", razorpayPaymentId);
        attributes.put("razorpay_signature", razorpaySignature);

        try {

            return Utils.verifyPaymentSignature(
                    attributes,
                    keySecret
            );

        } catch (RazorpayException ex) {
            return false;
        }
    }

    @Override
    public boolean verifyWebhookSignature(
            String payload,
            String webhookSignature) {

        if (payload == null || payload.isBlank()) {
            return false;
        }

        if (webhookSignature == null || webhookSignature.isBlank()) {
            return false;
        }

        if (webhookSecret == null || webhookSecret.isBlank()) {
            return false;
        }

        try {

            return Utils.verifyWebhookSignature(
                    payload,
                    webhookSignature,
                    webhookSecret
            );

        } catch (RazorpayException ex) {
            return false;
        }
    }
}