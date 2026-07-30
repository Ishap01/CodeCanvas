package com.codecanvas.paymentservice.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codecanvas.paymentservice.config.RazorpayConfig.RazorpayProperties;
import com.codecanvas.paymentservice.dto.request.CreatePaymentOrderRequest;
import com.codecanvas.paymentservice.dto.request.VerifyPaymentRequest;
import com.codecanvas.paymentservice.dto.response.PaymentOrderResponse;
import com.codecanvas.paymentservice.dto.response.PaymentResponse;
import com.codecanvas.paymentservice.dto.response.PaymentVerificationResponse;
import com.codecanvas.paymentservice.entity.Payment;
import com.codecanvas.paymentservice.enums.Currency;
import com.codecanvas.paymentservice.enums.PaymentStatus;
import com.codecanvas.paymentservice.repository.PaymentRepository;
import com.codecanvas.paymentservice.service.PaymentService;
import com.codecanvas.paymentservice.service.RazorpayService;
import com.codecanvas.paymentservice.service.SubscriptionActivationService;
import com.razorpay.Order;
import com.razorpay.RazorpayException;

import jakarta.persistence.EntityNotFoundException;

@Service
public class PaymentServiceImpl implements PaymentService {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(PaymentServiceImpl.class);

    private final PaymentRepository paymentRepository;
    private final RazorpayService razorpayService;
    private final SubscriptionActivationService subscriptionActivationService;
    private final RazorpayProperties razorpayProperties;
    private final BigDecimal premiumPlanAmount;

    public PaymentServiceImpl(
            PaymentRepository paymentRepository,
            RazorpayService razorpayService,
            SubscriptionActivationService subscriptionActivationService,
            RazorpayProperties razorpayProperties,
            @Value("${payment.premium-plan.amount}")
            BigDecimal premiumPlanAmount) {

        this.paymentRepository = paymentRepository;
        this.razorpayService = razorpayService;
        this.subscriptionActivationService =
                subscriptionActivationService;
        this.razorpayProperties = razorpayProperties;
        this.premiumPlanAmount = premiumPlanAmount;
    }

    @Override
    @Transactional
    public PaymentOrderResponse createPaymentOrder(
            UUID userId,
            CreatePaymentOrderRequest request) {

        validateCreatePaymentRequest(userId, request);

        String receipt = generateReceipt();

        long amountInPaise =
                convertRupeesToPaise(premiumPlanAmount);

        try {
            Order razorpayOrder =
                    razorpayService.createOrder(
                            receipt,
                            amountInPaise
                    );

            String razorpayOrderId =
                    razorpayOrder.get("id").toString();

            Payment payment = new Payment();

            payment.setUserId(userId);
            payment.setSubscriptionPlanId(
                    request.getSubscriptionPlanId()
            );
            payment.setRazorpayOrderId(razorpayOrderId);
            payment.setAmount(premiumPlanAmount);
            payment.setCurrency(Currency.INR);
            payment.setStatus(PaymentStatus.PENDING);
            payment.setReceipt(receipt);

            Payment savedPayment =
                    paymentRepository.save(payment);

            LOGGER.info(
                    "Payment order created. paymentId={}, userId={}, razorpayOrderId={}",
                    savedPayment.getPaymentId(),
                    userId,
                    razorpayOrderId
            );

            return new PaymentOrderResponse(
                    savedPayment.getPaymentId(),
                    savedPayment.getSubscriptionPlanId(),
                    savedPayment.getRazorpayOrderId(),
                    savedPayment.getAmount(),
                    amountInPaise,
                    savedPayment.getCurrency(),
                    savedPayment.getStatus(),
                    razorpayProperties.getKeyId(),
                    savedPayment.getReceipt()
            );

        } catch (RazorpayException exception) {

            LOGGER.error(
                    "Razorpay order creation failed for userId={}",
                    userId,
                    exception
            );

            throw new IllegalStateException(
                    "Unable to create Razorpay payment order",
                    exception
            );
        }
    }

    @Override
    @Transactional
    public PaymentVerificationResponse verifyPayment(
            UUID userId,
            VerifyPaymentRequest request) {

        validateVerifyPaymentRequest(userId, request);

        Payment payment = paymentRepository
                .findByRazorpayOrderId(
                        request.getRazorpayOrderId()
                )
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Payment not found for Razorpay order ID: "
                                        + request.getRazorpayOrderId()
                        )
                );

        validatePaymentOwnership(userId, payment);

        if (payment.getStatus() == PaymentStatus.SUCCESS
                || payment.getStatus() == PaymentStatus.CAPTURED) {

            boolean premiumActivated =
                    subscriptionActivationService
                            .activatePremiumSubscription(
                                    userId,
                                    payment
                            );

            return buildVerificationResponse(
                    payment,
                    true,
                    premiumActivated
            );
        }

        if (payment.getStatus() == PaymentStatus.CANCELLED) {
            throw new IllegalStateException(
                    "Cancelled payment cannot be verified"
            );
        }

        if (payment.getStatus() == PaymentStatus.REFUNDED) {
            throw new IllegalStateException(
                    "Refunded payment cannot be verified again"
            );
        }

        boolean signatureValid =
                razorpayService.verifyPaymentSignature(
                        request.getRazorpayOrderId(),
                        request.getRazorpayPaymentId(),
                        request.getRazorpaySignature()
                );

        if (!signatureValid) {

            payment.setStatus(PaymentStatus.FAILED);
            payment.setFailureReason(
                    "Razorpay payment signature verification failed"
            );

            paymentRepository.save(payment);

            LOGGER.warn(
                    "Payment signature verification failed. paymentId={}, razorpayOrderId={}",
                    payment.getPaymentId(),
                    payment.getRazorpayOrderId()
            );

            return buildVerificationResponse(
                    payment,
                    false,
                    false
            );
        }

        validateRazorpayPaymentId(
                payment,
                request.getRazorpayPaymentId()
        );

        payment.setRazorpayPaymentId(
                request.getRazorpayPaymentId()
        );

        payment.setRazorpaySignature(
                request.getRazorpaySignature()
        );

        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setVerifiedAt(LocalDateTime.now());
        payment.setFailureReason(null);

        Payment verifiedPayment =
                paymentRepository.save(payment);

        boolean premiumActivated =
                subscriptionActivationService
                        .activatePremiumSubscription(
                                userId,
                                verifiedPayment
                        );

        LOGGER.info(
                "Payment verified. paymentId={}, userId={}, premiumActivated={}",
                verifiedPayment.getPaymentId(),
                userId,
                premiumActivated
        );

        return buildVerificationResponse(
                verifiedPayment,
                true,
                premiumActivated
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentById(
            UUID paymentId) {

        if (paymentId == null) {
            throw new IllegalArgumentException(
                    "Payment ID is required"
            );
        }

        Payment payment = paymentRepository
                .findById(paymentId)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Payment not found with ID: "
                                        + paymentId
                        )
                );

        return mapToPaymentResponse(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> getUserPayments(
            UUID userId) {

        if (userId == null) {
            throw new IllegalArgumentException(
                    "User ID is required"
            );
        }

        return paymentRepository
                .findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::mapToPaymentResponse)
                .toList();
    }

    private void validateCreatePaymentRequest(
            UUID userId,
            CreatePaymentOrderRequest request) {

        if (userId == null) {
            throw new IllegalArgumentException(
                    "Authenticated user ID is required"
            );
        }

        if (request == null) {
            throw new IllegalArgumentException(
                    "Create payment order request is required"
            );
        }

        if (request.getSubscriptionPlanId() == null) {
            throw new IllegalArgumentException(
                    "Subscription plan ID is required"
            );
        }

        if (premiumPlanAmount == null
                || premiumPlanAmount.compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalStateException(
                    "Premium plan amount configuration is invalid"
            );
        }
    }

    private void validateVerifyPaymentRequest(
            UUID userId,
            VerifyPaymentRequest request) {

        if (userId == null) {
            throw new IllegalArgumentException(
                    "Authenticated user ID is required"
            );
        }

        if (request == null) {
            throw new IllegalArgumentException(
                    "Payment verification request is required"
            );
        }

        if (request.getRazorpayOrderId() == null
                || request.getRazorpayOrderId().isBlank()) {

            throw new IllegalArgumentException(
                    "Razorpay order ID is required"
            );
        }

        if (request.getRazorpayPaymentId() == null
                || request.getRazorpayPaymentId().isBlank()) {

            throw new IllegalArgumentException(
                    "Razorpay payment ID is required"
            );
        }

        if (request.getRazorpaySignature() == null
                || request.getRazorpaySignature().isBlank()) {

            throw new IllegalArgumentException(
                    "Razorpay signature is required"
            );
        }
    }

    private void validatePaymentOwnership(
            UUID userId,
            Payment payment) {

        if (!userId.equals(payment.getUserId())) {
            throw new IllegalStateException(
                    "Authenticated user does not own this payment"
            );
        }
    }

    private void validateRazorpayPaymentId(
            Payment payment,
            String razorpayPaymentId) {

        paymentRepository
                .findByRazorpayPaymentId(razorpayPaymentId)
                .ifPresent(existingPayment -> {

                    if (!existingPayment.getPaymentId()
                            .equals(payment.getPaymentId())) {

                        throw new IllegalStateException(
                                "Razorpay payment ID is already associated "
                                        + "with another payment"
                        );
                    }
                });
    }

    private String generateReceipt() {

        return "receipt_"
                + UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 20);
    }

    private long convertRupeesToPaise(
            BigDecimal amountInRupees) {

        return amountInRupees
                .setScale(2, RoundingMode.UNNECESSARY)
                .movePointRight(2)
                .longValueExact();
    }

    private PaymentVerificationResponse buildVerificationResponse(
            Payment payment,
            boolean verified,
            boolean premiumActivated) {

        return new PaymentVerificationResponse(
                payment.getPaymentId(),
                payment.getRazorpayOrderId(),
                payment.getRazorpayPaymentId(),
                payment.getStatus(),
                verified,
                premiumActivated,
                payment.getVerifiedAt()
        );
    }

    private PaymentResponse mapToPaymentResponse(
            Payment payment) {

        return new PaymentResponse(
                payment.getPaymentId(),
                payment.getUserId(),
                payment.getSubscriptionPlanId(),
                payment.getRazorpayOrderId(),
                payment.getRazorpayPaymentId(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getStatus(),
                payment.getReceipt(),
                payment.getFailureReason(),
                payment.getVerifiedAt(),
                payment.getCreatedAt(),
                payment.getUpdatedAt()
        );
    }
}