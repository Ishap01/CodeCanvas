package com.codecanvas.paymentservice.service.impl;

import com.codecanvas.paymentservice.service.RazorpayService;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RazorpayServiceImpl implements RazorpayService {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(RazorpayServiceImpl.class);

    private final RazorpayClient razorpayClient;
    private final String keySecret;
    private final String webhookSecret;

    public RazorpayServiceImpl(
            RazorpayClient razorpayClient,
            @Value("${razorpay.key-secret}") String keySecret,
            @Value("${razorpay.webhook-secret:}") String webhookSecret) {

        this.razorpayClient = razorpayClient;
        this.keySecret = keySecret;
        this.webhookSecret = webhookSecret;
    }

    @Override
    public Order createOrder(
            String receipt,
            long amountInPaise)
            throws RazorpayException {

        if (receipt == null || receipt.isBlank()) {
            throw new IllegalArgumentException(
                    "Receipt cannot be empty."
            );
        }

        if (amountInPaise <= 0) {
            throw new IllegalArgumentException(
                    "Amount must be greater than zero."
            );
        }

        JSONObject orderRequest = new JSONObject();

        orderRequest.put("amount", amountInPaise);
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", receipt);

        LOGGER.info(
                "Creating Razorpay order. receipt={}, amountInPaise={}, currency=INR",
                receipt,
                amountInPaise
        );

        try {
            Order order =
                    razorpayClient.orders.create(orderRequest);

            LOGGER.info(
                    "Razorpay order created successfully. razorpayOrderId={}, receipt={}",
                    order.get("id"),
                    receipt
            );

            return order;

        } catch (RazorpayException exception) {

            LOGGER.error(
                    "Razorpay order creation failed. receipt={}, amountInPaise={}, error={}",
                    receipt,
                    amountInPaise,
                    exception.getMessage(),
                    exception
            );

            System.err.println(
                    "===== RAZORPAY ORDER CREATION ERROR ====="
            );

            System.err.println(
                    "Receipt: " + receipt
            );

            System.err.println(
                    "Amount in paise: " + amountInPaise
            );

            System.err.println(
                    "Razorpay error message: "
                            + exception.getMessage()
            );

            exception.printStackTrace();

            throw exception;
        }
    }

    @Override
    public boolean verifyPaymentSignature(
            String razorpayOrderId,
            String razorpayPaymentId,
            String razorpaySignature) {

        if (razorpayOrderId == null
                || razorpayOrderId.isBlank()) {

            LOGGER.warn(
                    "Payment signature verification failed because order ID is missing"
            );

            return false;
        }

        if (razorpayPaymentId == null
                || razorpayPaymentId.isBlank()) {

            LOGGER.warn(
                    "Payment signature verification failed because payment ID is missing"
            );

            return false;
        }

        if (razorpaySignature == null
                || razorpaySignature.isBlank()) {

            LOGGER.warn(
                    "Payment signature verification failed because signature is missing"
            );

            return false;
        }

        JSONObject attributes = new JSONObject();

        attributes.put(
                "razorpay_order_id",
                razorpayOrderId
        );

        attributes.put(
                "razorpay_payment_id",
                razorpayPaymentId
        );

        attributes.put(
                "razorpay_signature",
                razorpaySignature
        );

        try {
            boolean signatureValid =
                    Utils.verifyPaymentSignature(
                            attributes,
                            keySecret
                    );

            LOGGER.info(
                    "Payment signature verification completed. razorpayOrderId={}, valid={}",
                    razorpayOrderId,
                    signatureValid
            );

            return signatureValid;

        } catch (RazorpayException exception) {

            LOGGER.error(
                    "Payment signature verification failed. razorpayOrderId={}, error={}",
                    razorpayOrderId,
                    exception.getMessage(),
                    exception
            );

            return false;
        }
    }

    @Override
    public boolean verifyWebhookSignature(
            String payload,
            String webhookSignature) {

        if (payload == null || payload.isBlank()) {

            LOGGER.warn(
                    "Webhook signature verification failed because payload is missing"
            );

            return false;
        }

        if (webhookSignature == null
                || webhookSignature.isBlank()) {

            LOGGER.warn(
                    "Webhook signature verification failed because signature is missing"
            );

            return false;
        }

        if (webhookSecret == null
                || webhookSecret.isBlank()) {

            LOGGER.error(
                    "Webhook signature verification failed because webhook secret is not configured"
            );

            return false;
        }

        try {
            boolean signatureValid =
                    Utils.verifyWebhookSignature(
                            payload,
                            webhookSignature,
                            webhookSecret
                    );

            LOGGER.info(
                    "Razorpay webhook signature verification completed. valid={}",
                    signatureValid
            );

            return signatureValid;

        } catch (RazorpayException exception) {

            LOGGER.error(
                    "Razorpay webhook signature verification failed. error={}",
                    exception.getMessage(),
                    exception
            );

            return false;
        }
    }
}