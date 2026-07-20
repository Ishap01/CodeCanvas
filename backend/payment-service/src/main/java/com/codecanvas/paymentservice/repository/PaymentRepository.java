package com.codecanvas.paymentservice.repository;

import com.codecanvas.paymentservice.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
}