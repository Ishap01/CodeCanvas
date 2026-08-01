package com.codecanvas.paymentservice.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codecanvas.paymentservice.dto.request.CreateOrderRequest;
import com.codecanvas.paymentservice.dto.request.CreateSubscriptionRequest;
import com.codecanvas.paymentservice.dto.request.VerifyPaymentRequest;
import com.codecanvas.paymentservice.dto.response.PaymentResponse;
import com.codecanvas.paymentservice.dto.response.RazorpayOrderResponse;
import com.codecanvas.paymentservice.dto.response.SubscriptionPlanResponse;
import com.codecanvas.paymentservice.entity.Payment;
import com.codecanvas.paymentservice.enums.PaymentStatus;
import com.codecanvas.paymentservice.enums.WebhookProcessingStatus;
import com.codecanvas.paymentservice.exception.InvalidSignatureException;
import com.codecanvas.paymentservice.exception.PaymentNotFoundException;
import com.codecanvas.paymentservice.exception.PaymentProcessingException;
import com.codecanvas.paymentservice.exception.UserServiceIntegrationException;
import com.codecanvas.paymentservice.feign.UserServiceClient;
import com.codecanvas.paymentservice.mapper.PaymentMapper;
import com.codecanvas.paymentservice.repository.PaymentRepository;
import com.codecanvas.paymentservice.security.AuthenticatedUser;
import com.codecanvas.paymentservice.service.PaymentService;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;

import jakarta.servlet.http.HttpServletRequest;

@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(PaymentServiceImpl.class);

    private static final String RAZORPAY_PAYMENT_METHOD =
            "RAZORPAY";

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final UserServiceClient userServiceClient;
    private final RazorpayClient razorpayClient;
    private final HttpServletRequest httpServletRequest;

    @Value("${razorpay.key-id}")
    private String razorpayKey;

    @Value("${razorpay.key-secret}")
    private String razorpaySecret;

    public PaymentServiceImpl(
            PaymentRepository paymentRepository,
            PaymentMapper paymentMapper,
            UserServiceClient userServiceClient,
            RazorpayClient razorpayClient,
            HttpServletRequest httpServletRequest) {

        this.paymentRepository = paymentRepository;
        this.paymentMapper = paymentMapper;
        this.userServiceClient = userServiceClient;
        this.razorpayClient = razorpayClient;
        this.httpServletRequest = httpServletRequest;
    }

    @Override
    public RazorpayOrderResponse createOrder(
            CreateOrderRequest request) {

        UUID userId = getCurrentAuthenticatedUserId();

        SubscriptionPlanResponse plan;

        try {
            plan = userServiceClient.getPlanById(
                    request.getPlanId()
            );

        } catch (Exception exception) {

            throw new UserServiceIntegrationException(
                    "Unable to fetch subscription plan from User Service.",
                    exception
            );
        }

        if (plan == null) {
            throw new UserServiceIntegrationException(
                    "Subscription plan not found."
            );
        }

        if (plan.getPrice() == null
                || plan.getPrice().compareTo(BigDecimal.ZERO) <= 0) {

            throw new PaymentProcessingException(
                    "Free plans do not require Razorpay payment."
            );
        }

        String receipt = generateReceipt();

        Payment payment = Payment.builder()
                .userId(userId)
                .planId(plan.getId())
                .planName(plan.getName())
                .amount(plan.getPrice())
                .currency(plan.getCurrency())
                .receipt(receipt)
                .paymentStatus(PaymentStatus.CREATED)
                .build();

        payment = paymentRepository.save(payment);

        try {
            Order razorpayOrder = createRazorpayOrder(
                    plan,
                    receipt
            );

            payment.setRazorpayOrderId(
                    razorpayOrder.get("id")
            );

            payment.setPaymentStatus(
                    PaymentStatus.PENDING
            );

            payment = paymentRepository.save(payment);

            return RazorpayOrderResponse.builder()
                    .paymentId(payment.getPaymentId())
                    .razorpayOrderId(
                            payment.getRazorpayOrderId()
                    )
                    .razorpayKey(razorpayKey)
                    .amount(
                            convertToPaise(plan.getPrice())
                    )
                    .currency(
                            plan.getCurrency().name()
                    )
                    .receipt(receipt)
                    .build();

        } catch (Exception exception) {

            LOGGER.error(
                    "Razorpay order creation failed. paymentId={}, error={}",
                    payment.getPaymentId(),
                    exception.getMessage(),
                    exception
            );

            payment.setPaymentStatus(
                    PaymentStatus.FAILED
            );

            payment.setFailureReason(
                    exception.getMessage()
            );

            paymentRepository.save(payment);

            throw new PaymentProcessingException(
                    "Failed to create Razorpay order: "
                            + exception.getMessage(),
                    exception
            );
        }
    }

    @Override
    public PaymentResponse verifyPayment(
            VerifyPaymentRequest request) {

        Payment payment = paymentRepository
                .findByRazorpayOrderId(
                        request.getRazorpayOrderId()
                )
                .orElseThrow(() ->
                        new PaymentNotFoundException(
                                "Payment not found for the given Razorpay Order ID."
                        )
                );

        /*
         * Same verify request dobara aaye to naya payment record
         * ya duplicate subscription create nahi karenge.
         */
        if (payment.getPaymentStatus()
                == PaymentStatus.SUCCESS) {

            return paymentMapper.toResponse(payment);
        }

        if (!verifySignature(request)) {
            throw new InvalidSignatureException(
                    "Invalid Razorpay payment signature."
            );
        }

        payment.setRazorpayPaymentId(
                request.getRazorpayPaymentId()
        );

        payment.setRazorpaySignature(
                request.getRazorpaySignature()
        );

        payment.setPaymentStatus(
                PaymentStatus.SUCCESS
        );

        payment.setWebhookProcessingStatus(
                WebhookProcessingStatus.PROCESSED
        );

        payment.setPaidAt(
                LocalDateTime.now()
        );

        Payment savedPayment =
                paymentRepository.save(payment);

        /*
         * Payment successfully save hone ke baad
         * User Service mein premium subscription activate hogi.
         */
        activatePremiumSubscription(savedPayment);

        return paymentMapper.toResponse(savedPayment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentById(
            UUID paymentId) {

        Payment payment = paymentRepository
                .findById(paymentId)
                .orElseThrow(() ->
                        new PaymentNotFoundException(
                                "Payment not found."
                        )
                );

        UUID currentUserId =
                getCurrentAuthenticatedUserId();

        if (!payment.getUserId()
                .equals(currentUserId)) {

            throw new PaymentNotFoundException(
                    "Payment not found."
            );
        }

        return paymentMapper.toResponse(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> getMyPayments() {

        UUID currentUserId =
                getCurrentAuthenticatedUserId();

        return paymentRepository
                .findByUserIdOrderByCreatedAtDesc(
                        currentUserId
                )
                .stream()
                .map(paymentMapper::toResponse)
                .toList();
    }

    private void activatePremiumSubscription(
            Payment payment) {

        String authorizationHeader =
                httpServletRequest.getHeader(
                        HttpHeaders.AUTHORIZATION
                );

        if (authorizationHeader == null
                || authorizationHeader.isBlank()) {

            LOGGER.error(
                    "Premium activation skipped because Authorization header "
                            + "is missing. paymentId={}, userId={}",
                    payment.getPaymentId(),
                    payment.getUserId()
            );

            return;
        }

        if (payment.getRazorpayPaymentId() == null
                || payment.getRazorpayPaymentId().isBlank()) {

            LOGGER.error(
                    "Premium activation skipped because Razorpay payment ID "
                            + "is missing. paymentId={}",
                    payment.getPaymentId()
            );

            return;
        }

        CreateSubscriptionRequest subscriptionRequest =
                new CreateSubscriptionRequest(
                        payment.getUserId(),
                        payment.getPlanId(),
                        payment.getRazorpayPaymentId(),
                        RAZORPAY_PAYMENT_METHOD
                );

        try {
            ResponseEntity<Void> response =
                    userServiceClient.createSubscription(
                            authorizationHeader,
                            subscriptionRequest
                    );

            if (response.getStatusCode().is2xxSuccessful()) {

                LOGGER.info(
                        "Premium subscription activated successfully. "
                                + "userId={}, planId={}, paymentId={}",
                        payment.getUserId(),
                        payment.getPlanId(),
                        payment.getRazorpayPaymentId()
                );

            } else {

                LOGGER.error(
                        "User Service returned non-success response during "
                                + "premium activation. status={}, userId={}, paymentId={}",
                        response.getStatusCode(),
                        payment.getUserId(),
                        payment.getPaymentId()
                );
            }

        } catch (Exception exception) {

            /*
             * Payment actual Razorpay transaction successful hai.
             * User Service failure ke karan payment ko FAILED/PENDING
             * mein rollback nahi karna chahiye.
             *
             * Future mein scheduler/Kafka retry is case ko handle karega.
             */
            LOGGER.error(
                    "Payment succeeded but premium activation failed. "
                            + "userId={}, planId={}, paymentId={}, error={}",
                    payment.getUserId(),
                    payment.getPlanId(),
                    payment.getRazorpayPaymentId(),
                    exception.getMessage(),
                    exception
            );
        }
    }

    private UUID getCurrentAuthenticatedUserId() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null
                || !(authentication.getPrincipal()
                instanceof AuthenticatedUser authenticatedUser)) {

            throw new PaymentProcessingException(
                    "Unable to identify authenticated user."
            );
        }

        return authenticatedUser.getUserId();
    }

    private String generateReceipt() {

        return "CC-"
                + UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 10)
                .toUpperCase();
    }

    private Order createRazorpayOrder(
            SubscriptionPlanResponse plan,
            String receipt)
            throws RazorpayException {

        JSONObject orderRequest =
                new JSONObject();

        orderRequest.put(
                "amount",
                convertToPaise(plan.getPrice())
        );

        orderRequest.put(
                "currency",
                plan.getCurrency().name()
        );

        orderRequest.put(
                "receipt",
                receipt
        );

        return razorpayClient
                .orders
                .create(orderRequest);
    }

    private boolean verifySignature(
            VerifyPaymentRequest request) {

        try {
            JSONObject attributes =
                    new JSONObject();

            attributes.put(
                    "razorpay_order_id",
                    request.getRazorpayOrderId()
            );

            attributes.put(
                    "razorpay_payment_id",
                    request.getRazorpayPaymentId()
            );

            attributes.put(
                    "razorpay_signature",
                    request.getRazorpaySignature()
            );

            return Utils.verifyPaymentSignature(
                    attributes,
                    razorpaySecret
            );

        } catch (Exception exception) {

            LOGGER.error(
                    "Razorpay signature verification failed. orderId={}, error={}",
                    request.getRazorpayOrderId(),
                    exception.getMessage(),
                    exception
            );

            return false;
        }
    }

    private long convertToPaise(
            BigDecimal amount) {

        return amount
                .multiply(BigDecimal.valueOf(100))
                .longValue();
    }
}