package com.codecanvas.paymentservice.service;

import com.codecanvas.paymentservice.dto.request.CreateOrderRequest;
import com.codecanvas.paymentservice.dto.request.VerifyPaymentRequest;
import com.codecanvas.paymentservice.dto.response.PaymentResponse;
import com.codecanvas.paymentservice.dto.response.RazorpayOrderResponse;

import java.util.List;
import java.util.UUID;

public interface PaymentService {

    RazorpayOrderResponse createOrder(CreateOrderRequest request);

    PaymentResponse verifyPayment(VerifyPaymentRequest request);

    PaymentResponse getPaymentById(UUID paymentId);

    List<PaymentResponse> getMyPayments();
}