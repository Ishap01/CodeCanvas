package com.codecanvas.paymentservice.dto.request;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreateRefundRequest {

    @NotNull(message = "Payment ID is required")
    private UUID paymentId;

    @NotNull(message = "Refund amount is required")
    @DecimalMin(
            value = "1.00",
            inclusive = true,
            message = "Refund amount must be at least 1.00"
    )
    private BigDecimal amount;

    @NotBlank(message = "Refund reason is required")
    @Size(max = 500, message = "Refund reason cannot exceed 500 characters")
    private String reason;

    public CreateRefundRequest() {
    }

    public CreateRefundRequest(
            UUID paymentId,
            BigDecimal amount,
            String reason) {

        this.paymentId = paymentId;
        this.amount = amount;
        this.reason = reason;
    }

    public UUID getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(UUID paymentId) {
        this.paymentId = paymentId;
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
}