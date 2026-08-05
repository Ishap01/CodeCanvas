package com.codecanvas.paymentservice.dto.request;

import jakarta.validation.constraints.NotBlank;

public class MarkPaymentFailedRequest {

    @NotBlank(message = "Razorpay order ID is required.")
    private String razorpayOrderId;

    private String razorpayPaymentId;

    @NotBlank(message = "Failure reason is required.")
    private String failureReason;

    public MarkPaymentFailedRequest() {
    }

    public MarkPaymentFailedRequest(
            String razorpayOrderId,
            String razorpayPaymentId,
            String failureReason) {

        this.razorpayOrderId = razorpayOrderId;
        this.razorpayPaymentId = razorpayPaymentId;
        this.failureReason = failureReason;
    }

    public String getRazorpayOrderId() {
        return razorpayOrderId;
    }

    public void setRazorpayOrderId(
            String razorpayOrderId) {

        this.razorpayOrderId = razorpayOrderId;
    }

    public String getRazorpayPaymentId() {
        return razorpayPaymentId;
    }

    public void setRazorpayPaymentId(
            String razorpayPaymentId) {

        this.razorpayPaymentId = razorpayPaymentId;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(
            String failureReason) {

        this.failureReason = failureReason;
    }
}