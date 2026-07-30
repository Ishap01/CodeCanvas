package com.codecanvas.paymentservice.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.codecanvas.paymentservice.entity.Refund;
import com.codecanvas.paymentservice.enums.RefundStatus;

@Repository
public interface RefundRepository
        extends JpaRepository<Refund, UUID> {

    Optional<Refund> findByRazorpayRefundId(
            String razorpayRefundId
    );

    boolean existsByRazorpayRefundId(
            String razorpayRefundId
    );

    List<Refund>
    findByPaymentPaymentIdOrderByCreatedAtDesc(
            UUID paymentId
    );

    List<Refund>
    findByStatusOrderByCreatedAtAsc(
            RefundStatus status
    );
}