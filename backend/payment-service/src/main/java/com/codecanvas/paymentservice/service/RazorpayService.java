package com.codecanvas.paymentservice.service;

import com.razorpay.Order;
import com.razorpay.RazorpayException;

public interface RazorpayService {

    Order createOrder(
            String receipt,
            long amountInPaise
    ) throws RazorpayException;

    boolean verifyPaymentSignature(
            String razorpayOrderId,
            String razorpayPaymentId,
            String razorpaySignature
    );

    boolean verifyWebhookSignature(
            String payload,
            String webhookSignature
    );
}