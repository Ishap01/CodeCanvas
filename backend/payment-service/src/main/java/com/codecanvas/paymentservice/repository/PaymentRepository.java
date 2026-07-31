package com.codecanvas.paymentservice.repository;

import com.codecanvas.paymentservice.entity.Payment;
import com.codecanvas.paymentservice.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    /**
     * Find payment using Razorpay Order ID.
     */
    Optional<Payment> findByRazorpayOrderId(String razorpayOrderId);

    /**
     * Find payment using Razorpay Payment ID.
     */
    Optional<Payment> findByRazorpayPaymentId(String razorpayPaymentId);

    /**
     * Get payment history of a user.
     */
    List<Payment> findByUserIdOrderByCreatedAtDesc(UUID userId);

    /**
     * Get all payments having a particular status.
     */
    List<Payment> findByPaymentStatus(PaymentStatus paymentStatus);

    /**
     * Get payments of a user having a particular status.
     */
    List<Payment> findByUserIdAndPaymentStatusOrderByCreatedAtDesc(
            UUID userId,
            PaymentStatus paymentStatus
    );

    /**
     * Check duplicate Razorpay payment.
     */
    boolean existsByRazorpayPaymentId(String razorpayPaymentId);

    /**
     * Used by scheduler to expire old pending payments.
     */
    List<Payment> findByPaymentStatusAndCreatedAtBefore(
            PaymentStatus paymentStatus,
            LocalDateTime createdAt
    );

}