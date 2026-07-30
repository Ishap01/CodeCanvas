package com.codecanvas.paymentservice.service;

import java.util.List;
import java.util.UUID;

import com.codecanvas.paymentservice.dto.request.CreateRefundRequest;
import com.codecanvas.paymentservice.dto.response.RefundResponse;

public interface RefundService {

    RefundResponse createRefund(
            UUID requestedByUserId,
            CreateRefundRequest request
    );

    RefundResponse getRefundById(
            UUID refundId
    );

    List<RefundResponse> getRefundsByPaymentId(
            UUID paymentId
    );

    List<RefundResponse> getAllRefunds();
}