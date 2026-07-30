package com.codecanvas.paymentservice.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codecanvas.paymentservice.dto.request.CreateRefundRequest;
import com.codecanvas.paymentservice.dto.response.RefundResponse;
import com.codecanvas.paymentservice.entity.Payment;
import com.codecanvas.paymentservice.entity.Refund;
import com.codecanvas.paymentservice.enums.PaymentStatus;
import com.codecanvas.paymentservice.enums.RefundStatus;
import com.codecanvas.paymentservice.repository.PaymentRepository;
import com.codecanvas.paymentservice.repository.RefundRepository;
import com.codecanvas.paymentservice.service.RazorpayService;
import com.codecanvas.paymentservice.service.RefundService;
import com.razorpay.RazorpayException;

import jakarta.persistence.EntityNotFoundException;

@Service
public class RefundServiceImpl implements RefundService {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(RefundServiceImpl.class);

    private final RefundRepository refundRepository;
    private final PaymentRepository paymentRepository;
    private final RazorpayService razorpayService;

    public RefundServiceImpl(
            RefundRepository refundRepository,
            PaymentRepository paymentRepository,
            RazorpayService razorpayService) {

        this.refundRepository = refundRepository;
        this.paymentRepository = paymentRepository;
        this.razorpayService = razorpayService;
    }

    @Override
    @Transactional
    public RefundResponse createRefund(
            UUID requestedByUserId,
            CreateRefundRequest request) {

        validateRefundRequest(
                requestedByUserId,
                request
        );

        Payment payment = paymentRepository
                .findById(request.getPaymentId())
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Payment not found with ID: "
                                        + request.getPaymentId()
                        )
                );

        validatePaymentOwnership(
                requestedByUserId,
                payment
        );

        validatePaymentForRefund(
                payment,
                request.getAmount()
        );

        BigDecimal alreadyRefundedAmount =
                calculateExistingRefundAmount(
                        payment.getPaymentId()
                );

        BigDecimal totalRefundAmount =
                alreadyRefundedAmount.add(
                        request.getAmount()
                );

        if (totalRefundAmount.compareTo(
                payment.getAmount()) > 0) {

            throw new IllegalStateException(
                    "Total refund amount cannot exceed payment amount"
            );
        }

        Refund refund = new Refund();

        refund.setPayment(payment);
        refund.setAmount(request.getAmount());
        refund.setReason(request.getReason().trim());
        refund.setStatus(RefundStatus.CREATED);

        Refund savedRefund =
                refundRepository.save(refund);

        try {
            com.razorpay.Refund razorpayRefund =
                    razorpayService.createRefund(
                            payment.getRazorpayPaymentId(),
                            request
                    );

            String razorpayRefundId =
                    razorpayRefund.get("id").toString();

            String razorpayRefundStatus =
                    razorpayRefund.has("status")
                            ? razorpayRefund
                            .get("status")
                            .toString()
                            : "pending";

            savedRefund.setRazorpayRefundId(
                    razorpayRefundId
            );

            updateRefundStatus(
                    savedRefund,
                    razorpayRefundStatus
            );

            if (savedRefund.getStatus()
                    == RefundStatus.PROCESSED) {

                savedRefund.setProcessedAt(
                        LocalDateTime.now()
                );

                updatePaymentAfterProcessedRefund(
                        payment,
                        totalRefundAmount
                );

            } else if (savedRefund.getStatus()
                    == RefundStatus.PENDING) {

                payment.setStatus(
                        PaymentStatus.REFUND_PENDING
                );

                paymentRepository.save(payment);
            }

            Refund updatedRefund =
                    refundRepository.save(savedRefund);

            LOGGER.info(
                    "Refund created. refundId={}, paymentId={}, razorpayRefundId={}, status={}",
                    updatedRefund.getRefundId(),
                    payment.getPaymentId(),
                    updatedRefund.getRazorpayRefundId(),
                    updatedRefund.getStatus()
            );

            return mapToRefundResponse(updatedRefund);

        } catch (RazorpayException exception) {

            savedRefund.setStatus(
                    RefundStatus.FAILED
            );

            savedRefund.setFailureReason(
                    exception.getMessage()
            );

            refundRepository.save(savedRefund);

            LOGGER.error(
                    "Razorpay refund creation failed. refundId={}, paymentId={}",
                    savedRefund.getRefundId(),
                    payment.getPaymentId(),
                    exception
            );

            throw new IllegalStateException(
                    "Unable to create Razorpay refund",
                    exception
            );
        }
    }

    @Override
    @Transactional(readOnly = true)
    public RefundResponse getRefundById(
            UUID refundId) {

        if (refundId == null) {
            throw new IllegalArgumentException(
                    "Refund ID is required"
            );
        }

        Refund refund = refundRepository
                .findById(refundId)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Refund not found with ID: "
                                        + refundId
                        )
                );

        return mapToRefundResponse(refund);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RefundResponse> getRefundsByPaymentId(
            UUID paymentId) {

        if (paymentId == null) {
            throw new IllegalArgumentException(
                    "Payment ID is required"
            );
        }

        if (!paymentRepository.existsById(paymentId)) {
            throw new EntityNotFoundException(
                    "Payment not found with ID: "
                            + paymentId
            );
        }

        return refundRepository
                .findByPaymentPaymentIdOrderByCreatedAtDesc(
                        paymentId
                )
                .stream()
                .map(this::mapToRefundResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RefundResponse> getAllRefunds() {

        return refundRepository
                .findAll()
                .stream()
                .map(this::mapToRefundResponse)
                .toList();
    }

    private void validateRefundRequest(
            UUID requestedByUserId,
            CreateRefundRequest request) {

        if (requestedByUserId == null) {
            throw new IllegalArgumentException(
                    "Authenticated requester ID is required"
            );
        }

        if (request == null) {
            throw new IllegalArgumentException(
                    "Refund request is required"
            );
        }

        if (request.getPaymentId() == null) {
            throw new IllegalArgumentException(
                    "Payment ID is required"
            );
        }

        if (request.getAmount() == null
                || request.getAmount()
                .compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                    "Refund amount must be greater than zero"
            );
        }

        if (request.getReason() == null
                || request.getReason().isBlank()) {

            throw new IllegalArgumentException(
                    "Refund reason is required"
            );
        }
    }

    private void validatePaymentOwnership(
            UUID requestedByUserId,
            Payment payment) {

        if (!requestedByUserId.equals(
                payment.getUserId())) {

            throw new IllegalStateException(
                    "Authenticated user does not own this payment"
            );
        }
    }

    private void validatePaymentForRefund(
            Payment payment,
            BigDecimal refundAmount) {

        if (payment.getStatus() != PaymentStatus.SUCCESS
                && payment.getStatus() != PaymentStatus.CAPTURED
                && payment.getStatus()
                != PaymentStatus.PARTIALLY_REFUNDED) {

            throw new IllegalStateException(
                    "Only successful, captured or partially "
                            + "refunded payments can be refunded"
            );
        }

        if (payment.getRazorpayPaymentId() == null
                || payment.getRazorpayPaymentId().isBlank()) {

            throw new IllegalStateException(
                    "Razorpay payment ID is not available"
            );
        }

        if (refundAmount.compareTo(
                payment.getAmount()) > 0) {

            throw new IllegalStateException(
                    "Refund amount cannot exceed payment amount"
            );
        }
    }

    private BigDecimal calculateExistingRefundAmount(
            UUID paymentId) {

        return refundRepository
                .findByPaymentPaymentIdOrderByCreatedAtDesc(
                        paymentId
                )
                .stream()
                .filter(refund ->
                        refund.getStatus()
                                != RefundStatus.FAILED
                                && refund.getStatus()
                                != RefundStatus.CANCELLED
                )
                .map(Refund::getAmount)
                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add
                );
    }

    private void updateRefundStatus(
            Refund refund,
            String razorpayStatus) {

        if ("processed".equalsIgnoreCase(
                razorpayStatus)) {

            refund.setStatus(
                    RefundStatus.PROCESSED
            );

            refund.setFailureReason(null);

        } else if ("failed".equalsIgnoreCase(
                razorpayStatus)) {

            refund.setStatus(
                    RefundStatus.FAILED
            );

            refund.setFailureReason(
                    "Razorpay marked refund as failed"
            );

        } else {
            refund.setStatus(
                    RefundStatus.PENDING
            );

            refund.setFailureReason(null);
        }
    }

    private void updatePaymentAfterProcessedRefund(
            Payment payment,
            BigDecimal totalRefundAmount) {

        if (totalRefundAmount.compareTo(
                payment.getAmount()) == 0) {

            payment.setStatus(
                    PaymentStatus.REFUNDED
            );

        } else {
            payment.setStatus(
                    PaymentStatus.PARTIALLY_REFUNDED
            );
        }

        paymentRepository.save(payment);
    }

    private RefundResponse mapToRefundResponse(
            Refund refund) {

        return new RefundResponse(
                refund.getRefundId(),
                refund.getPayment().getPaymentId(),
                refund.getPayment().getRazorpayPaymentId(),
                refund.getRazorpayRefundId(),
                refund.getAmount(),
                refund.getReason(),
                refund.getStatus(),
                refund.getFailureReason(),
                refund.getProcessedAt(),
                refund.getCreatedAt(),
                refund.getUpdatedAt()
        );
    }
}