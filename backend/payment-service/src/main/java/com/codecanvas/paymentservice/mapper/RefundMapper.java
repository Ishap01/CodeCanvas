package com.codecanvas.paymentservice.mapper;

import org.springframework.stereotype.Component;

import com.codecanvas.paymentservice.dto.response.RefundResponse;
import com.codecanvas.paymentservice.entity.Refund;

@Component
public class RefundMapper {

    public RefundMapper() {
    }

    public RefundResponse toRefundResponse(
            Refund refund) {

        if (refund == null) {
            return null;
        }

        return new RefundResponse(
                refund.getRefundId(),
                refund.getPayment().getPaymentId(),
                refund.getPayment().getRazorpayPaymentId(),
                refund.getRazorpayRefundId(),
                refund.getAmount(),
                refund.getReason(),
                refund.getStatus(),
                refund.getFailureReason(),
                refund.getProcessedAt(),
                refund.getCreatedAt(),
                refund.getUpdatedAt()
        );
    }
}