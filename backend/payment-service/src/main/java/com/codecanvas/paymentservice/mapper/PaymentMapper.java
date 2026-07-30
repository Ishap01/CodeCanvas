package com.codecanvas.paymentservice.mapper;

import org.springframework.stereotype.Component;

import com.codecanvas.paymentservice.dto.response.PaymentResponse;
import com.codecanvas.paymentservice.entity.Payment;

@Component
public class PaymentMapper {

    public PaymentMapper() {
    }

    public PaymentResponse toPaymentResponse(
            Payment payment) {

        if (payment == null) {
            return null;
        }

        return new PaymentResponse(
                payment.getPaymentId(),
                payment.getUserId(),
                payment.getSubscriptionPlanId(),
                payment.getRazorpayOrderId(),
                payment.getRazorpayPaymentId(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getStatus(),
                payment.getReceipt(),
                payment.getFailureReason(),
                payment.getVerifiedAt(),
                payment.getCreatedAt(),
                payment.getUpdatedAt()
        );
    }
}