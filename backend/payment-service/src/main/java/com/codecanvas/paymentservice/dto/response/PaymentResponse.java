package com.codecanvas.paymentservice.dto.response;

import com.codecanvas.paymentservice.enums.Currency;
import com.codecanvas.paymentservice.enums.PaymentStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponse {

    /**
     * Internal Payment ID
     */
    private UUID paymentId;

    /**
     * User who made the payment
     */
    private UUID userId;

    /**
     * Purchased Subscription Plan
     */
    private Long planId;

    /**
     * Payment Amount
     */
    private BigDecimal amount;

    /**
     * Payment Currency
     */
    private Currency currency;

    /**
     * Current Payment Status
     */
    private PaymentStatus paymentStatus;

    /**
     * Refund Status
     */
//    private RefundStatus refundStatus;

    /**
     * Razorpay Order ID
     */
    private String razorpayOrderId;

    /**
     * Razorpay Payment ID
     */
    private String razorpayPaymentId;

    /**
     * Payment Method
     */
    private String paymentMethod;

    /**
     * Receipt Number
     */
    private String receipt;

    /**
     * Failure Reason
     */
    private String failureReason;

    /**
     * Payment Time
     */
    private LocalDateTime paidAt;

    /**
     * Created Time
     */
    private LocalDateTime createdAt;

    private String planName;
}