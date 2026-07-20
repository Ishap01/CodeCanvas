package com.codecanvas.paymentservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "payment_id")
    private UUID paymentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_id", nullable = false)
    private Subscription subscription;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(name = "payment_method", length = 10)
    private String paymentMethod;

    @Column(name = "transaction_id", length = 30)
    private String transactionId;

    @Column(name = "razorpay_payment_id", length = 150)
    private String razorpayPaymentId;

    @Column(name = "payment_status", length = 20)
    private String paymentStatus;

    @Column(name = "payment_date")
    private LocalDateTime paymentDate;
}