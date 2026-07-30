package com.codecanvas.paymentservice.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.codecanvas.paymentservice.enums.RefundStatus;

public class RefundResponse {

    private UUID refundId;
    private UUID paymentId;
    private String razorpayPaymentId;
    private String razorpayRefundId;
    private BigDecimal amount;
    private String reason;
    private RefundStatus status;
    private String failureReason;
    private LocalDateTime processedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public RefundResponse() {
    }

    public RefundResponse(
            UUID refundId,
            UUID paymentId,
            String razorpayPaymentId,
            String razorpayRefundId,
            BigDecimal amount,
            String reason,
            RefundStatus status,
            String failureReason,
            LocalDateTime processedAt,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {

        this.refundId = refundId;
        this.paymentId = paymentId;
        this.razorpayPaymentId = razorpayPaymentId;
        this.razorpayRefundId = razorpayRefundId;
        this.amount = amount;
        this.reason = reason;
        this.status = status;
        this.failureReason = failureReason;
        this.processedAt = processedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getRefundId() {
        return refundId;
    }

    public void setRefundId(UUID refundId) {
        this.refundId = refundId;
    }

    public UUID getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(UUID paymentId) {
        this.paymentId = paymentId;
    }

    public String getRazorpayPaymentId() {
        return razorpayPaymentId;
    }

    public void setRazorpayPaymentId(String razorpayPaymentId) {
        this.razorpayPaymentId = razorpayPaymentId;
    }

    public String getRazorpayRefundId() {
        return razorpayRefundId;
    }

    public void setRazorpayRefundId(String razorpayRefundId) {
        this.razorpayRefundId = razorpayRefundId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public RefundStatus getStatus() {
        return status;
    }

    public void setStatus(RefundStatus status) {
        this.status = status;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(LocalDateTime processedAt) {
        this.processedAt = processedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}