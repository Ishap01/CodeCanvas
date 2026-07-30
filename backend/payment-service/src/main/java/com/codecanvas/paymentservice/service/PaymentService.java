package com.codecanvas.paymentservice.service;

import java.util.List;
import java.util.UUID;

import com.codecanvas.paymentservice.dto.request.CreatePaymentOrderRequest;
import com.codecanvas.paymentservice.dto.request.VerifyPaymentRequest;
import com.codecanvas.paymentservice.dto.response.PaymentOrderResponse;
import com.codecanvas.paymentservice.dto.response.PaymentResponse;
import com.codecanvas.paymentservice.dto.response.PaymentVerificationResponse;

public interface PaymentService {

    PaymentOrderResponse createPaymentOrder(
            UUID userId,
            CreatePaymentOrderRequest request
    );

    PaymentVerificationResponse verifyPayment(
            UUID userId,
            VerifyPaymentRequest request
    );

    PaymentResponse getPaymentById(
            UUID paymentId
    );

    List<PaymentResponse> getUserPayments(
            UUID userId
    );

}