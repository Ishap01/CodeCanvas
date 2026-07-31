//package com.codecanvas.paymentservice.service.impl;
//
//import com.codecanvas.paymentservice.dto.request.CreateOrderRequest;
//import com.codecanvas.paymentservice.dto.request.VerifyPaymentRequest;
//import com.codecanvas.paymentservice.dto.response.PaymentResponse;
//import com.codecanvas.paymentservice.dto.response.RazorpayOrderResponse;
//import com.codecanvas.paymentservice.dto.response.SubscriptionPlanResponse;
//import com.codecanvas.paymentservice.entity.Payment;
//import com.codecanvas.paymentservice.enums.PaymentStatus;
//import com.codecanvas.paymentservice.exception.PaymentProcessingException;
//import com.codecanvas.paymentservice.exception.ResourceNotFoundException;
//import com.codecanvas.paymentservice.exception.UserServiceIntegrationException;
//import com.codecanvas.paymentservice.feign.UserServiceClient;
//import com.codecanvas.paymentservice.mapper.PaymentMapper;
//import com.codecanvas.paymentservice.repository.PaymentRepository;
//import com.codecanvas.paymentservice.security.AuthenticatedUser;
//import com.codecanvas.paymentservice.service.PaymentService;
//import com.razorpay.Order;
//import com.razorpay.RazorpayClient;
//import lombok.RequiredArgsConstructor;
//import org.json.JSONObject;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import com.codecanvas.paymentservice.enums.WebhookProcessingStatus;
//import com.codecanvas.paymentservice.exception.InvalidSignatureException;
//import com.codecanvas.paymentservice.exception.PaymentNotFoundException;
//import com.razorpay.Utils;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.UUID;
//
//@Service
//@RequiredArgsConstructor
//@Transactional
//public class PaymentServiceImpl implements PaymentService {
//
//    private final PaymentRepository paymentRepository;
//
//    private final PaymentMapper paymentMapper;
//
//    private final UserServiceClient userServiceClient;
//
//    private final RazorpayClient razorpayClient;
//
//    @Value("${razorpay.key-id}")
//    private String razorpayKey;
//
//    @Value("${razorpay.key-secret}")
//    private String razorpaySecret;
//
//    @Override
//    public RazorpayOrderResponse createOrder(CreateOrderRequest request) {
//
//        UUID userId = getCurrentAuthenticatedUserId();
//
//        SubscriptionPlanResponse plan;
//
//        try {
//
//            plan = userServiceClient.getPlanById(request.getPlanId());
//
//        } catch (Exception ex) {
//
//            throw new UserServiceIntegrationException(
//                    "Unable to fetch subscription plan from User Service.",
//                    ex
//            );
//        }
//
//        if (plan == null) {
//            throw new UserServiceIntegrationException(
//                    "Subscription plan not found."
//            );
//        }
//
//        String receipt = generateReceipt();
//
//        Payment payment = Payment.builder()
//                .userId(userId)
//                .planId(plan.getId())
//                .planName(plan.getName())
//                .amount(plan.getPrice())
//                .currency(plan.getCurrency())
//                .receipt(receipt)
//                .paymentStatus(PaymentStatus.CREATED)
//                .build();
//
//        payment = paymentRepository.save(payment);
//
//        try {
//
//            Order razorpayOrder = createRazorpayOrder(
//                    plan,
//                    receipt
//            );
//
//            payment.setRazorpayOrderId(
//                    razorpayOrder.get("id")
//            );
//
//            payment.setPaymentStatus(
//                    PaymentStatus.PENDING
//            );
//
//            paymentRepository.save(payment);
//
//            return RazorpayOrderResponse.builder()
//                    .paymentId(payment.getPaymentId())
//                    .razorpayOrderId(payment.getRazorpayOrderId())
//                    .razorpayKey(razorpayKey)
//                    .amount(
//                            convertToPaise(plan.getPrice())
//                    )
//                    .currency(
//                            plan.getCurrency().name()
//                    )
//                    .receipt(receipt)
//                    .build();
//
//        } catch (Exception ex) {
//
//            payment.setPaymentStatus(
//                    PaymentStatus.FAILED
//            );
//
//            payment.setFailureReason(
//                    ex.getMessage()
//            );
//
//            paymentRepository.save(payment);
//
//            throw new PaymentProcessingException(
//                    "Failed to create Razorpay order.",
//                    ex
//            );
//        }
//
//    }
//
//    @Override
//    public PaymentResponse verifyPayment(
//            VerifyPaymentRequest request) {
//
//        Payment payment = paymentRepository
//                .findByRazorpayOrderId(request.getRazorpayOrderId())
//                .orElseThrow(() ->
//                        new PaymentNotFoundException(
//                                "Payment not found for the given Razorpay Order ID."
//                        ));
//
//        if (payment.getPaymentStatus() == PaymentStatus.SUCCESS) {
//            return paymentMapper.toResponse(payment);
//        }
//
//        if (!verifySignature(request)) {
//            throw new InvalidSignatureException(
//                    "Invalid Razorpay payment signature."
//            );
//        }
//
//        payment.setRazorpayPaymentId(
//                request.getRazorpayPaymentId()
//        );
//
//        payment.setRazorpaySignature(
//                request.getRazorpaySignature()
//        );
//
//        payment.setPaymentStatus(
//                PaymentStatus.SUCCESS
//        );
//
//        payment.setWebhookProcessingStatus(
//                WebhookProcessingStatus.PROCESSED
//        );
//
//        payment.setPaidAt(
//                LocalDateTime.now()
//        );
//
//        Payment savedPayment = paymentRepository.save(payment);
//
//        return paymentMapper.toResponse(savedPayment);
//    }
//
//    @Override
//    @Transactional(readOnly = true)
//    public PaymentResponse getPaymentById(
//            UUID paymentId) {
//
//        Payment payment = paymentRepository
//                .findById(paymentId)
//                .orElseThrow(() ->
//                        new PaymentNotFoundException(
//                                "Payment not found."
//                        ));
//
//        UUID currentUser = getCurrentAuthenticatedUserId();
//
//        if (!payment.getUserId().equals(currentUser)) {
//            throw new PaymentNotFoundException(
//                    "Payment not found."
//            );
//        }
//
//        return paymentMapper.toResponse(payment);
//    }
//
//    @Override
//    @Transactional(readOnly = true)
//    public List<PaymentResponse> getMyPayments() {
//
//        UUID currentUser = getCurrentAuthenticatedUserId();
//
//        return paymentRepository
//                .findByUserIdOrderByCreatedAtDesc(currentUser)
//                .stream()
//                .map(paymentMapper::toResponse)
//                .toList();
//    }
//
//// ==========================================================
//// Helper Methods
//// ==========================================================
//
//    private UUID getCurrentAuthenticatedUserId() {
//
//        Authentication authentication =
//                SecurityContextHolder.getContext().getAuthentication();
//
//        if (authentication == null ||
//                !(authentication.getPrincipal() instanceof AuthenticatedUser authenticatedUser)) {
//
//            throw new PaymentProcessingException(
//                    "Unable to identify authenticated user."
//            );
//        }
//
//        return authenticatedUser.getUserId();
//    }
//
//    private String generateReceipt() {
//
//        return "CC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
//    }
//
//    private Order createRazorpayOrder(
//            SubscriptionPlanResponse plan,
//            String receipt) throws Exception {
//
//        JSONObject orderRequest = new JSONObject();
//
//        orderRequest.put(
//                "amount",
//                convertToPaise(plan.getPrice())
//        );
//
//        orderRequest.put(
//                "currency",
//                plan.getCurrency().name()
//        );
//
//        orderRequest.put(
//                "receipt",
//                receipt
//        );
//
//        return razorpayClient.orders.create(orderRequest);
//    }
//
//    private boolean verifySignature(
//            VerifyPaymentRequest request) {
//
//        try {
//
//            JSONObject attributes = new JSONObject();
//
//            attributes.put(
//                    "razorpay_order_id",
//                    request.getRazorpayOrderId()
//            );
//
//            attributes.put(
//                    "razorpay_payment_id",
//                    request.getRazorpayPaymentId()
//            );
//
//            attributes.put(
//                    "razorpay_signature",
//                    request.getRazorpaySignature()
//            );
//
//            return Utils.verifyPaymentSignature(
//                    attributes,
//                    razorpaySecret
//            );
//
//        } catch (Exception ex) {
//
//            return false;
//        }
//    }
//
//    private long convertToPaise(
//            BigDecimal amount) {
//
//        return amount
//                .multiply(BigDecimal.valueOf(100))
//                .longValue();
//    }


package com.codecanvas.paymentservice.service.impl;

import com.codecanvas.paymentservice.dto.request.CreateOrderRequest;
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
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    private final PaymentMapper paymentMapper;

    private final UserServiceClient userServiceClient;

    private final RazorpayClient razorpayClient;

    @Value("${razorpay.key-id}")
    private String razorpayKey;

    @Value("${razorpay.key-secret}")
    private String razorpaySecret;

    @Override
    public RazorpayOrderResponse createOrder(CreateOrderRequest request) {

        UUID userId = getCurrentAuthenticatedUserId();

        SubscriptionPlanResponse plan;

        try {
            plan = userServiceClient.getPlanById(request.getPlanId());
        } catch (Exception ex) {
//            throw new UserServiceIntegrationException(
//                    "Unable to fetch subscription plan from User Service.",
//                    ex
//            );

            ex.printStackTrace();

            throw ex;

        }

        if (plan == null) {
            throw new UserServiceIntegrationException(
                    "Subscription plan not found."
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
                    .razorpayOrderId(payment.getRazorpayOrderId())
                    .razorpayKey(razorpayKey)
                    .amount(convertToPaise(plan.getPrice()))
                    .currency(plan.getCurrency().name())
                    .receipt(receipt)
                    .build();

        } catch (Exception ex) {

            payment.setPaymentStatus(PaymentStatus.FAILED);
            payment.setFailureReason(ex.getMessage());

            paymentRepository.save(payment);

            throw new PaymentProcessingException(
                    "Failed to create Razorpay order.",
                    ex
            );
        }
    }

    @Override
    public PaymentResponse verifyPayment(
            VerifyPaymentRequest request) {

        Payment payment = paymentRepository
                .findByRazorpayOrderId(request.getRazorpayOrderId())
                .orElseThrow(() ->
                        new PaymentNotFoundException(
                                "Payment not found for the given Razorpay Order ID."
                        ));

        if (payment.getPaymentStatus() == PaymentStatus.SUCCESS) {
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

        Payment savedPayment = paymentRepository.save(payment);

        return paymentMapper.toResponse(savedPayment);
    }
    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentById(UUID paymentId) {

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() ->
                        new PaymentNotFoundException("Payment not found."));

        UUID currentUserId = getCurrentAuthenticatedUserId();

        if (!payment.getUserId().equals(currentUserId)) {
            throw new PaymentNotFoundException("Payment not found.");
        }

        return paymentMapper.toResponse(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> getMyPayments() {

        UUID currentUserId = getCurrentAuthenticatedUserId();

        return paymentRepository
                .findByUserIdOrderByCreatedAtDesc(currentUserId)
                .stream()
                .map(paymentMapper::toResponse)
                .toList();
    }

    // ==========================================================
    // Helper Methods
    // ==========================================================

    private UUID getCurrentAuthenticatedUserId() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null ||
                !(authentication.getPrincipal() instanceof AuthenticatedUser authenticatedUser)) {

            throw new PaymentProcessingException(
                    "Unable to identify authenticated user."
            );
        }

        return authenticatedUser.getUserId();
    }

    private String generateReceipt() {

        return "CC-" +
                UUID.randomUUID()
                        .toString()
                        .replace("-", "")
                        .substring(0, 10)
                        .toUpperCase();
    }

    private Order createRazorpayOrder(
            SubscriptionPlanResponse plan,
            String receipt) throws RazorpayException {

        JSONObject orderRequest = new JSONObject();

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

        return razorpayClient.orders.create(orderRequest);
    }

    private boolean verifySignature(
            VerifyPaymentRequest request) {

        try {

            JSONObject attributes = new JSONObject();

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

        } catch (Exception ex) {

            return false;
        }
    }

    private long convertToPaise(BigDecimal amount) {

        return amount
                .multiply(BigDecimal.valueOf(100))
                .longValue();
    }
}


