package com.codecanvas.paymentservice.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

import com.codecanvas.paymentservice.enums.Currency;
import com.codecanvas.paymentservice.enums.PaymentStatus;

public class PaymentOrderResponse {

    private UUID paymentId;
    private UUID subscriptionPlanId;
    private String razorpayOrderId;
    private BigDecimal amount;
    private Long amountInPaise;
    private Currency currency;
    private PaymentStatus status;
    private String razorpayKeyId;
    private String receipt;

    public PaymentOrderResponse() {
    }

    public PaymentOrderResponse(
            UUID paymentId,
            UUID subscriptionPlanId,
            String razorpayOrderId,
            BigDecimal amount,
            Long amountInPaise,
            Currency currency,
            PaymentStatus status,
            String razorpayKeyId,
            String receipt) {

        this.paymentId = paymentId;
        this.subscriptionPlanId = subscriptionPlanId;
        this.razorpayOrderId = razorpayOrderId;
        this.amount = amount;
        this.amountInPaise = amountInPaise;
        this.currency = currency;
        this.status = status;
        this.razorpayKeyId = razorpayKeyId;
        this.receipt = receipt;
    }

    public UUID getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(UUID paymentId) {
        this.paymentId = paymentId;
    }

    public UUID getSubscriptionPlanId() {
        return subscriptionPlanId;
    }

    public void setSubscriptionPlanId(UUID subscriptionPlanId) {
        this.subscriptionPlanId = subscriptionPlanId;
    }

    public String getRazorpayOrderId() {
        return razorpayOrderId;
    }

    public void setRazorpayOrderId(String razorpayOrderId) {
        this.razorpayOrderId = razorpayOrderId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Long getAmountInPaise() {
        return amountInPaise;
    }

    public void setAmountInPaise(Long amountInPaise) {
        this.amountInPaise = amountInPaise;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public String getRazorpayKeyId() {
        return razorpayKeyId;
    }

    public void setRazorpayKeyId(String razorpayKeyId) {
        this.razorpayKeyId = razorpayKeyId;
    }

    public String getReceipt() {
        return receipt;
    }

    public void setReceipt(String receipt) {
        this.receipt = receipt;
    }
}