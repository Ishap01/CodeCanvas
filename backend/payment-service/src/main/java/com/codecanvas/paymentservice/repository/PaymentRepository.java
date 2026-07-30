package com.codecanvas.paymentservice.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.codecanvas.paymentservice.entity.Payment;
import com.codecanvas.paymentservice.enums.PaymentStatus;

@Repository
public interface PaymentRepository
        extends JpaRepository<Payment, UUID> {

    Optional<Payment> findByRazorpayOrderId(
            String razorpayOrderId
    );

    Optional<Payment> findByRazorpayPaymentId(
            String razorpayPaymentId
    );

    boolean existsByRazorpayOrderId(
            String razorpayOrderId
    );

    boolean existsByRazorpayPaymentId(
            String razorpayPaymentId
    );

    List<Payment> findByUserIdOrderByCreatedAtDesc(
            UUID userId
    );

    List<Payment> findByStatusAndCreatedAtBefore(
            PaymentStatus status,
            LocalDateTime createdBefore
    );
}