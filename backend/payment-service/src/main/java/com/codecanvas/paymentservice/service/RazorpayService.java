package com.codecanvas.paymentservice.service;

import com.codecanvas.paymentservice.dto.request.CreateRefundRequest;
import com.razorpay.Order;
import com.razorpay.Refund;
import com.razorpay.RazorpayException;

public interface RazorpayService {

    Order createOrder(
            String receipt,
            Long amountInPaise
    ) throws RazorpayException;

    boolean verifyPaymentSignature(
            String razorpayOrderId,
            String razorpayPaymentId,
            String razorpaySignature
    );

    Refund createRefund(
            String razorpayPaymentId,
            CreateRefundRequest request
    ) throws RazorpayException;

    boolean verifyWebhookSignature(
            String payload,
            String webhookSignature
    );

}