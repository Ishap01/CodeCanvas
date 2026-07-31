package com.codecanvas.paymentservice.mapper;

import com.codecanvas.paymentservice.dto.response.PaymentResponse;
import com.codecanvas.paymentservice.entity.Payment;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

    public PaymentResponse toResponse(Payment payment) {

        if (payment == null) {
            return null;
        }

        return PaymentResponse.builder()
                .paymentId(payment.getPaymentId())
                .userId(payment.getUserId())
                .planId(payment.getPlanId())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .paymentStatus(payment.getPaymentStatus())
//                .refundStatus(payment.getRefundStatus())
                .razorpayOrderId(payment.getRazorpayOrderId())
                .razorpayPaymentId(payment.getRazorpayPaymentId())
                .paymentMethod(payment.getPaymentMethod())
                .receipt(payment.getReceipt())
                .failureReason(payment.getFailureReason())
                .paidAt(payment.getPaidAt())
                .createdAt(payment.getCreatedAt())
                .planName(payment.getPlanName())
                .build();
    }

}