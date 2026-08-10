package com.codecanvas.paymentservice.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.codecanvas.paymentservice.enums.PaymentStatus;

public class PaymentVerificationResponse {

    private UUID paymentId;
    private String razorpayOrderId;
    private String razorpayPaymentId;
    private PaymentStatus status;
    private boolean verified;
    private boolean premiumActivated;
    private LocalDateTime verifiedAt;

    public PaymentVerificationResponse() {
    }

    public PaymentVerificationResponse(
            UUID paymentId,
            String razorpayOrderId,
            String razorpayPaymentId,
            PaymentStatus status,
            boolean verified,
            boolean premiumActivated,
            LocalDateTime verifiedAt) {

        this.paymentId = paymentId;
        this.razorpayOrderId = razorpayOrderId;
        this.razorpayPaymentId = razorpayPaymentId;
        this.status = status;
        this.verified = verified;
        this.premiumActivated = premiumActivated;
        this.verifiedAt = verifiedAt;
    }

    public UUID getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(UUID paymentId) {
        this.paymentId = paymentId;
    }

    public String getRazorpayOrderId() {
        return razorpayOrderId;
    }

    public void setRazorpayOrderId(String razorpayOrderId) {
        this.razorpayOrderId = razorpayOrderId;
    }

    public String getRazorpayPaymentId() {
        return razorpayPaymentId;
    }

    public void setRazorpayPaymentId(String razorpayPaymentId) {
        this.razorpayPaymentId = razorpayPaymentId;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public boolean isPremiumActivated() {
        return premiumActivated;
    }

    public void setPremiumActivated(boolean premiumActivated) {
        this.premiumActivated = premiumActivated;
    }

    public LocalDateTime getVerifiedAt() {
        return verifiedAt;
    }

    public void setVerifiedAt(LocalDateTime verifiedAt) {
        this.verifiedAt = verifiedAt;
    }
}