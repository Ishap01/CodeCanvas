package com.codecanvas.paymentservice.scheduler;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.codecanvas.paymentservice.entity.Payment;
import com.codecanvas.paymentservice.enums.PaymentStatus;
import com.codecanvas.paymentservice.repository.PaymentRepository;

@Component
public class PaymentStatusScheduler {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(PaymentStatusScheduler.class);

    private final PaymentRepository paymentRepository;
    private final long pendingPaymentExpiryMinutes;

    public PaymentStatusScheduler(
            PaymentRepository paymentRepository,
            @Value("${payment.scheduler.pending-expiry-minutes:30}")
            long pendingPaymentExpiryMinutes) {

        this.paymentRepository = paymentRepository;
        this.pendingPaymentExpiryMinutes =
                pendingPaymentExpiryMinutes;
    }

    @Scheduled(
            fixedDelayString =
                    "${payment.scheduler.fixed-delay-milliseconds:300000}"
    )
    @Transactional
    public void markExpiredPendingPaymentsAsFailed() {

        LocalDateTime expiryTime =
                LocalDateTime.now()
                        .minusMinutes(
                                pendingPaymentExpiryMinutes
                        );

        List<Payment> expiredPendingPayments =
                paymentRepository
                        .findByStatusAndCreatedAtBefore(
                                PaymentStatus.PENDING,
                                expiryTime
                        );

        if (expiredPendingPayments.isEmpty()) {

            LOGGER.debug(
                    "No expired pending payments found"
            );

            return;
        }

        for (Payment payment :
                expiredPendingPayments) {

            payment.setStatus(
                    PaymentStatus.FAILED
            );

            payment.setFailureReason(
                    "Payment was not completed within "
                            + pendingPaymentExpiryMinutes
                            + " minutes"
            );
        }

        paymentRepository.saveAll(
                expiredPendingPayments
        );

        LOGGER.info(
                "{} expired pending payments marked as FAILED",
                expiredPendingPayments.size()
        );
    }
}