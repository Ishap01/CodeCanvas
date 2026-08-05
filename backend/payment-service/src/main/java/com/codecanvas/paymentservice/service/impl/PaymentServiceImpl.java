//package com.codecanvas.paymentservice.service.impl;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.UUID;
//
//import org.json.JSONObject;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.transaction.support.TransactionSynchronization;
//import org.springframework.transaction.support.TransactionSynchronizationManager;
//
//import com.codecanvas.paymentservice.dto.request.CreateOrderRequest;
//import com.codecanvas.paymentservice.dto.request.CreateSubscriptionRequest;
//import com.codecanvas.paymentservice.dto.request.VerifyPaymentRequest;
//import com.codecanvas.paymentservice.dto.response.PaymentResponse;
//import com.codecanvas.paymentservice.dto.response.RazorpayOrderResponse;
//import com.codecanvas.paymentservice.dto.response.SubscriptionPlanResponse;
//import com.codecanvas.paymentservice.entity.Payment;
//import com.codecanvas.paymentservice.enums.PaymentStatus;
//import com.codecanvas.paymentservice.enums.WebhookProcessingStatus;
//import com.codecanvas.paymentservice.exception.InvalidSignatureException;
//import com.codecanvas.paymentservice.exception.PaymentNotFoundException;
//import com.codecanvas.paymentservice.exception.PaymentProcessingException;
//import com.codecanvas.paymentservice.exception.UserServiceIntegrationException;
//import com.codecanvas.paymentservice.feign.UserServiceClient;
//import com.codecanvas.paymentservice.kafka.event.PaymentSuccessEvent;
//import com.codecanvas.paymentservice.kafka.producer.PaymentEventProducer;
//import com.codecanvas.paymentservice.mapper.PaymentMapper;
//import com.codecanvas.paymentservice.repository.PaymentRepository;
//import com.codecanvas.paymentservice.security.AuthenticatedUser;
//import com.codecanvas.paymentservice.service.PaymentService;
//import com.razorpay.Order;
//import com.razorpay.RazorpayClient;
//import com.razorpay.RazorpayException;
//import com.razorpay.Utils;
//
//import jakarta.servlet.http.HttpServletRequest;
//
//@Service
//@Transactional
//public class PaymentServiceImpl implements PaymentService {
//
//    private static final Logger LOGGER =
//            LoggerFactory.getLogger(PaymentServiceImpl.class);
//
//    private static final String RAZORPAY_PAYMENT_METHOD =
//            "RAZORPAY";
//
//    private static final String PAYMENT_SUCCESS_EVENT_TYPE =
//            "PAYMENT_SUCCESS";
//
//    private final PaymentRepository paymentRepository;
//    private final PaymentMapper paymentMapper;
//    private final UserServiceClient userServiceClient;
//    private final RazorpayClient razorpayClient;
//    private final HttpServletRequest httpServletRequest;
//    private final PaymentEventProducer paymentEventProducer;
//
//    @Value("${razorpay.key-id}")
//    private String razorpayKey;
//
//    @Value("${razorpay.key-secret}")
//    private String razorpaySecret;
//
//    public PaymentServiceImpl(
//            PaymentRepository paymentRepository,
//            PaymentMapper paymentMapper,
//            UserServiceClient userServiceClient,
//            RazorpayClient razorpayClient,
//            HttpServletRequest httpServletRequest,
//            PaymentEventProducer paymentEventProducer) {
//
//        this.paymentRepository = paymentRepository;
//        this.paymentMapper = paymentMapper;
//        this.userServiceClient = userServiceClient;
//        this.razorpayClient = razorpayClient;
//        this.httpServletRequest = httpServletRequest;
//        this.paymentEventProducer = paymentEventProducer;
//    }
//
//    @Override
//    public RazorpayOrderResponse createOrder(
//            CreateOrderRequest request) {
//
//        UUID userId = getCurrentAuthenticatedUserId();
//
//        SubscriptionPlanResponse plan;
//
//        try {
//
//            /*
//             * Order create karne ke time plan ka price,
//             * name aur currency immediately chahiye.
//             *
//             * Isliye yahan Feign call rahega.
//             */
//            plan = userServiceClient.getPlanById(
//                    request.getPlanId()
//            );
//
//        } catch (Exception exception) {
//
//            throw new UserServiceIntegrationException(
//                    "Unable to fetch subscription plan from User Service.",
//                    exception
//            );
//        }
//
//        if (plan == null) {
//            throw new UserServiceIntegrationException(
//                    "Subscription plan not found."
//            );
//        }
//
//        if (plan.getPrice() == null
//                || plan.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
//
//            throw new PaymentProcessingException(
//                    "Free plans do not require Razorpay payment."
//            );
//        }
//
//        String receipt = generateReceipt();
//
//        /*
//         * Razorpay order banne se pehle apne database mein
//         * payment record CREATED status ke saath save karte hain.
//         */
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
//            /*
//             * Actual Razorpay order create hota hai.
//             */
//            Order razorpayOrder = createRazorpayOrder(
//                    plan,
//                    receipt
//            );
//
//            payment.setRazorpayOrderId(
//                    razorpayOrder.get("id")
//            );
//
//            /*
//             * Razorpay order create ho gaya hai,
//             * lekin payment abhi complete nahi hua.
//             */
//            payment.setPaymentStatus(
//                    PaymentStatus.PENDING
//            );
//
//            payment = paymentRepository.save(payment);
//
//            return RazorpayOrderResponse.builder()
//                    .paymentId(
//                            payment.getPaymentId()
//                    )
//                    .razorpayOrderId(
//                            payment.getRazorpayOrderId()
//                    )
//                    .razorpayKey(
//                            razorpayKey
//                    )
//                    .amount(
//                            convertToPaise(
//                                    plan.getPrice()
//                            )
//                    )
//                    .currency(
//                            plan.getCurrency().name()
//                    )
//                    .receipt(
//                            receipt
//                    )
//                    .build();
//
//        } catch (Exception exception) {
//
//            LOGGER.error(
//                    "Razorpay order creation failed. paymentId={}, error={}",
//                    payment.getPaymentId(),
//                    exception.getMessage(),
//                    exception
//            );
//
//            payment.setPaymentStatus(
//                    PaymentStatus.FAILED
//            );
//
//            payment.setFailureReason(
//                    exception.getMessage()
//            );
//
//            paymentRepository.save(payment);
//
//            throw new PaymentProcessingException(
//                    "Failed to create Razorpay order: "
//                            + exception.getMessage(),
//                    exception
//            );
//        }
//    }
//
//    @Override
//    public PaymentResponse verifyPayment(
//            VerifyPaymentRequest request) {
//
//        Payment payment = paymentRepository
//                .findByRazorpayOrderId(
//                        request.getRazorpayOrderId()
//                )
//                .orElseThrow(() ->
//                        new PaymentNotFoundException(
//                                "Payment not found for the given Razorpay Order ID."
//                        )
//                );
//
//        /*
//         * Same verify request dobara aane par:
//         *
//         * - payment dobara process nahi hoga
//         * - Kafka event dobara publish nahi hoga
//         * - subscription dobara activate nahi hogi
//         */
//        if (payment.getPaymentStatus()
//                == PaymentStatus.SUCCESS) {
//
//            LOGGER.info(
//                    "Payment already verified. Duplicate verification skipped. "
//                            + "paymentId={}, razorpayOrderId={}",
//                    payment.getPaymentId(),
//                    payment.getRazorpayOrderId()
//            );
//
//            return paymentMapper.toResponse(payment);
//        }
//
//        /*
//         * Razorpay signature verify karti hai ki order ID,
//         * payment ID aur signature genuine hain.
//         */
//        if (!verifySignature(request)) {
//
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
//        /*
//         * Payment ko sabse pehle database mein SUCCESS
//         * status ke saath save karte hain.
//         */
//        Payment savedPayment =
//                paymentRepository.save(payment);
//
//        /*
//         * Saved Payment entity se Kafka event banate hain.
//         */
//        PaymentSuccessEvent paymentSuccessEvent =
//                createPaymentSuccessEvent(
//                        savedPayment
//                );
//
//        /*
//         * Event database transaction commit hone ke baad
//         * Kafka par publish hoga.
//         */
//        publishPaymentSuccessAfterCommit(
//                paymentSuccessEvent
//        );
//
//        /*
//         * TEMPORARY:
//         *
//         * User Service Kafka consumer complete aur tested hone
//         * tak existing Feign activation rahega.
//         *
//         * Consumer ready hone ke baad ye call remove karna hai,
//         * warna Feign aur Kafka dono activation karenge.
//         */
//        activatePremiumSubscription(
//                savedPayment
//        );
//
//        return paymentMapper.toResponse(
//                savedPayment
//        );
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
//                        )
//                );
//
//        UUID currentUserId =
//                getCurrentAuthenticatedUserId();
//
//        if (!payment.getUserId()
//                .equals(currentUserId)) {
//
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
//        UUID currentUserId =
//                getCurrentAuthenticatedUserId();
//
//        return paymentRepository
//                .findByUserIdOrderByCreatedAtDesc(
//                        currentUserId
//                )
//                .stream()
//                .map(paymentMapper::toResponse)
//                .toList();
//    }
//
//    /**
//     * Payment database entity ko Kafka event DTO mein
//     * convert karta hai.
//     */
//    private PaymentSuccessEvent createPaymentSuccessEvent(
//            Payment payment) {
//
//        String currency = null;
//
//        if (payment.getCurrency() != null) {
//            currency = payment.getCurrency().name();
//        }
//
//        return new PaymentSuccessEvent(
//                UUID.randomUUID(),
//                PAYMENT_SUCCESS_EVENT_TYPE,
//                payment.getPaymentId(),
//                payment.getUserId(),
//                payment.getPlanId(),
//                payment.getPlanName(),
//                payment.getRazorpayPaymentId(),
//                payment.getAmount(),
//                currency,
//                LocalDateTime.now()
//        );
//    }
//
//    /**
//     * PaymentSuccessEvent ko database transaction commit
//     * hone ke baad Kafka par publish karta hai.
//     */
//    private void publishPaymentSuccessAfterCommit(
//            PaymentSuccessEvent event) {
//
//        /*
//         * Check karta hai ki current method kisi active
//         * database transaction ke andar chal raha hai ya nahi.
//         */
//        if (TransactionSynchronizationManager
//                .isActualTransactionActive()) {
//
//            /*
//             * Current transaction ke saath callback register hota hai.
//             */
//            TransactionSynchronizationManager
//                    .registerSynchronization(
//                            new TransactionSynchronization() {
//
//                                /*
//                                 * Ye method tab execute hoga jab database
//                                 * transaction successfully commit ho jayega.
//                                 */
//                                @Override
//                                public void afterCommit() {
//
//                                    LOGGER.info(
//                                            "Payment transaction committed. "
//                                                    + "Publishing Kafka event. "
//                                                    + "paymentId={}, eventId={}",
//                                            event.getPaymentId(),
//                                            event.getEventId()
//                                    );
//
//                                    paymentEventProducer
//                                            .publishPaymentSuccess(
//                                                    event
//                                            );
//                                }
//                            }
//                    );
//
//            return;
//        }
//
//        /*
//         * Normally verifyPayment() @Transactional ke andar chalega.
//         * Ye sirf safety fallback hai.
//         */
//        LOGGER.warn(
//                "No active transaction found. Publishing payment event "
//                        + "immediately. paymentId={}, eventId={}",
//                event.getPaymentId(),
//                event.getEventId()
//        );
//
//        paymentEventProducer.publishPaymentSuccess(
//                event
//        );
//    }
//
//    /**
//     * Existing Feign-based premium subscription activation.
//     *
//     * User Service Kafka consumer ready hone ke baad
//     * is method ko remove karenge.
//     */
//    private void activatePremiumSubscription(
//            Payment payment) {
//
//        String authorizationHeader =
//                httpServletRequest.getHeader(
//                        HttpHeaders.AUTHORIZATION
//                );
//
//        if (authorizationHeader == null
//                || authorizationHeader.isBlank()) {
//
//            LOGGER.error(
//                    "Premium activation skipped because Authorization header "
//                            + "is missing. paymentId={}, userId={}",
//                    payment.getPaymentId(),
//                    payment.getUserId()
//            );
//
//            return;
//        }
//
//        if (payment.getRazorpayPaymentId() == null
//                || payment.getRazorpayPaymentId().isBlank()) {
//
//            LOGGER.error(
//                    "Premium activation skipped because Razorpay payment ID "
//                            + "is missing. paymentId={}",
//                    payment.getPaymentId()
//            );
//
//            return;
//        }
//
//        CreateSubscriptionRequest subscriptionRequest =
//                new CreateSubscriptionRequest(
//                        payment.getUserId(),
//                        payment.getPlanId(),
//                        payment.getRazorpayPaymentId(),
//                        RAZORPAY_PAYMENT_METHOD
//                );
//
//        try {
//
//            ResponseEntity<Void> response =
//                    userServiceClient.createSubscription(
//                            authorizationHeader,
//                            subscriptionRequest
//                    );
//
//            if (response.getStatusCode()
//                    .is2xxSuccessful()) {
//
//                LOGGER.info(
//                        "Premium subscription activated successfully through Feign. "
//                                + "userId={}, planId={}, paymentId={}",
//                        payment.getUserId(),
//                        payment.getPlanId(),
//                        payment.getPaymentId()
//                );
//
//            } else {
//
//                LOGGER.error(
//                        "User Service returned non-success response during "
//                                + "premium activation. status={}, userId={}, paymentId={}",
//                        response.getStatusCode(),
//                        payment.getUserId(),
//                        payment.getPaymentId()
//                );
//            }
//
//        } catch (Exception exception) {
//
//            /*
//             * Razorpay payment successful ho chuka hai.
//             * User Service error ke karan successful payment
//             * ko rollback nahi karenge.
//             */
//            LOGGER.error(
//                    "Payment succeeded but premium activation failed. "
//                            + "userId={}, planId={}, paymentId={}, error={}",
//                    payment.getUserId(),
//                    payment.getPlanId(),
//                    payment.getPaymentId(),
//                    exception.getMessage(),
//                    exception
//            );
//        }
//    }
//
//    private UUID getCurrentAuthenticatedUserId() {
//
//        Authentication authentication =
//                SecurityContextHolder
//                        .getContext()
//                        .getAuthentication();
//
//        if (authentication == null
//                || !(authentication.getPrincipal()
//                instanceof AuthenticatedUser authenticatedUser)) {
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
//        return "CC-"
//                + UUID.randomUUID()
//                .toString()
//                .replace("-", "")
//                .substring(0, 10)
//                .toUpperCase();
//    }
//
//    private Order createRazorpayOrder(
//            SubscriptionPlanResponse plan,
//            String receipt)
//            throws RazorpayException {
//
//        JSONObject orderRequest =
//                new JSONObject();
//
//        orderRequest.put(
//                "amount",
//                convertToPaise(
//                        plan.getPrice()
//                )
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
//        return razorpayClient
//                .orders
//                .create(orderRequest);
//    }
//
//    private boolean verifySignature(
//            VerifyPaymentRequest request) {
//
//        try {
//
//            JSONObject attributes =
//                    new JSONObject();
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
//        } catch (Exception exception) {
//
//            LOGGER.error(
//                    "Razorpay signature verification failed. orderId={}, error={}",
//                    request.getRazorpayOrderId(),
//                    exception.getMessage(),
//                    exception
//            );
//
//            return false;
//        }
//    }
//
//    private long convertToPaise(
//            BigDecimal amount) {
//
//        return amount
//                .multiply(
//                        BigDecimal.valueOf(100)
//                )
//                .longValue();
//    }


//}




//package com.codecanvas.paymentservice.service.impl;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.UUID;
//
//import org.json.JSONObject;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.transaction.support.TransactionSynchronization;
//import org.springframework.transaction.support.TransactionSynchronizationManager;
//
//import com.codecanvas.paymentservice.dto.request.CreateOrderRequest;
//import com.codecanvas.paymentservice.dto.request.VerifyPaymentRequest;
//import com.codecanvas.paymentservice.dto.response.PaymentResponse;
//import com.codecanvas.paymentservice.dto.response.RazorpayOrderResponse;
//import com.codecanvas.paymentservice.dto.response.SubscriptionPlanResponse;
//import com.codecanvas.paymentservice.entity.Payment;
//import com.codecanvas.paymentservice.enums.PaymentStatus;
//import com.codecanvas.paymentservice.enums.WebhookProcessingStatus;
//import com.codecanvas.paymentservice.exception.InvalidSignatureException;
//import com.codecanvas.paymentservice.exception.PaymentNotFoundException;
//import com.codecanvas.paymentservice.exception.PaymentProcessingException;
//import com.codecanvas.paymentservice.exception.UserServiceIntegrationException;
//import com.codecanvas.paymentservice.feign.UserServiceClient;
//import com.codecanvas.paymentservice.kafka.event.PaymentSuccessEvent;
//import com.codecanvas.paymentservice.kafka.producer.PaymentEventProducer;
//import com.codecanvas.paymentservice.mapper.PaymentMapper;
//import com.codecanvas.paymentservice.repository.PaymentRepository;
//import com.codecanvas.paymentservice.security.AuthenticatedUser;
//import com.codecanvas.paymentservice.service.PaymentService;
//import com.razorpay.Order;
//import com.razorpay.RazorpayClient;
//import com.razorpay.RazorpayException;
//import com.razorpay.Utils;
//
//@Service
//@Transactional
//public class PaymentServiceImpl implements PaymentService {
//
//    private static final Logger LOGGER =
//            LoggerFactory.getLogger(PaymentServiceImpl.class);
//
//    private static final String PAYMENT_SUCCESS_EVENT_TYPE =
//            "PAYMENT_SUCCESS";
//
//    private final PaymentRepository paymentRepository;
//    private final PaymentMapper paymentMapper;
//    private final UserServiceClient userServiceClient;
//    private final RazorpayClient razorpayClient;
//    private final PaymentEventProducer paymentEventProducer;
//
//    @Value("${razorpay.key-id}")
//    private String razorpayKey;
//
//    @Value("${razorpay.key-secret}")
//    private String razorpaySecret;
//
//    public PaymentServiceImpl(
//            PaymentRepository paymentRepository,
//            PaymentMapper paymentMapper,
//            UserServiceClient userServiceClient,
//            RazorpayClient razorpayClient,
//            PaymentEventProducer paymentEventProducer) {
//
//        this.paymentRepository = paymentRepository;
//        this.paymentMapper = paymentMapper;
//        this.userServiceClient = userServiceClient;
//        this.razorpayClient = razorpayClient;
//        this.paymentEventProducer = paymentEventProducer;
//    }
//
//    @Override
//    public RazorpayOrderResponse createOrder(
//            CreateOrderRequest request) {
//
//        UUID userId = getCurrentAuthenticatedUserId();
//
//        SubscriptionPlanResponse plan;
//
//        try {
//
//            /*
//             * Order create karne ke time Payment Service ko
//             * plan ka price, name aur currency immediately chahiye.
//             *
//             * Isliye yahan Feign communication rahega.
//             */
//            plan = userServiceClient.getPlanById(
//                    request.getPlanId()
//            );
//
//        } catch (Exception exception) {
//
//            throw new UserServiceIntegrationException(
//                    "Unable to fetch subscription plan from User Service.",
//                    exception
//            );
//        }
//
//        if (plan == null) {
//            throw new UserServiceIntegrationException(
//                    "Subscription plan not found."
//            );
//        }
//
//        if (plan.getPrice() == null
//                || plan.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
//
//            throw new PaymentProcessingException(
//                    "Free plans do not require Razorpay payment."
//            );
//        }
//
//        String receipt = generateReceipt();
//
//        /*
//         * Razorpay order create karne se pehle
//         * apne database mein payment record banate hain.
//         */
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
//            /*
//             * Actual Razorpay order create hota hai.
//             */
//            Order razorpayOrder = createRazorpayOrder(
//                    plan,
//                    receipt
//            );
//
//            payment.setRazorpayOrderId(
//                    razorpayOrder.get("id")
//            );
//
//            /*
//             * Order create ho gaya hai,
//             * lekin payment abhi complete nahi hua.
//             */
//            payment.setPaymentStatus(
//                    PaymentStatus.PENDING
//            );
//
//            payment = paymentRepository.save(payment);
//
//            return RazorpayOrderResponse.builder()
//                    .paymentId(
//                            payment.getPaymentId()
//                    )
//                    .razorpayOrderId(
//                            payment.getRazorpayOrderId()
//                    )
//                    .razorpayKey(
//                            razorpayKey
//                    )
//                    .amount(
//                            convertToPaise(
//                                    plan.getPrice()
//                            )
//                    )
//                    .currency(
//                            plan.getCurrency().name()
//                    )
//                    .receipt(
//                            receipt
//                    )
//                    .build();
//
//        } catch (Exception exception) {
//
//            LOGGER.error(
//                    "Razorpay order creation failed. paymentId={}, error={}",
//                    payment.getPaymentId(),
//                    exception.getMessage(),
//                    exception
//            );
//
//            payment.setPaymentStatus(
//                    PaymentStatus.FAILED
//            );
//
//            payment.setFailureReason(
//                    exception.getMessage()
//            );
//
//            paymentRepository.save(payment);
//
//            throw new PaymentProcessingException(
//                    "Failed to create Razorpay order: "
//                            + exception.getMessage(),
//                    exception
//            );
//        }
//    }
//
//    @Override
//    public PaymentResponse verifyPayment(
//            VerifyPaymentRequest request) {
//
//        Payment payment = paymentRepository
//                .findByRazorpayOrderId(
//                        request.getRazorpayOrderId()
//                )
//                .orElseThrow(() ->
//                        new PaymentNotFoundException(
//                                "Payment not found for the given Razorpay Order ID."
//                        )
//                );
//
//        /*
//         * Same verify request dobara aaye to:
//         *
//         * - payment dobara process nahi hoga
//         * - Kafka event dobara publish nahi hoga
//         * - subscription dobara activate nahi hogi
//         */
//        if (payment.getPaymentStatus()
//                == PaymentStatus.SUCCESS) {
//
//            LOGGER.info(
//                    "Payment already verified. Duplicate verification skipped. "
//                            + "paymentId={}, razorpayOrderId={}",
//                    payment.getPaymentId(),
//                    payment.getRazorpayOrderId()
//            );
//
//            return paymentMapper.toResponse(
//                    payment
//            );
//        }
//
//        /*
//         * Razorpay signature verify karti hai ki
//         * order ID, payment ID aur signature genuine hain.
//         */
//        if (!verifySignature(request)) {
//
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
//        /*
//         * Sabse pehle payment database mein
//         * SUCCESS status ke saath save hota hai.
//         */
//        Payment savedPayment =
//                paymentRepository.save(payment);
//
//        /*
//         * Database entity se Kafka event create hota hai.
//         */
//        PaymentSuccessEvent paymentSuccessEvent =
//                createPaymentSuccessEvent(
//                        savedPayment
//                );
//
//        /*
//         * Event transaction commit hone ke baad
//         * Kafka par publish hoga.
//         */
//        publishPaymentSuccessAfterCommit(
//                paymentSuccessEvent
//        );
//
//        /*
//         * Subscription activation ab direct Feign se nahi hogi.
//         *
//         * User Service ka Kafka consumer event receive karke
//         * subscription activate karega.
//         */
//        return paymentMapper.toResponse(
//                savedPayment
//        );
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
//                        )
//                );
//
//        UUID currentUserId =
//                getCurrentAuthenticatedUserId();
//
//        if (!payment.getUserId()
//                .equals(currentUserId)) {
//
//            throw new PaymentNotFoundException(
//                    "Payment not found."
//            );
//        }
//
//        return paymentMapper.toResponse(
//                payment
//        );
//    }
//
//    @Override
//    @Transactional(readOnly = true)
//    public List<PaymentResponse> getMyPayments() {
//
//        UUID currentUserId =
//                getCurrentAuthenticatedUserId();
//
//        return paymentRepository
//                .findByUserIdOrderByCreatedAtDesc(
//                        currentUserId
//                )
//                .stream()
//                .map(paymentMapper::toResponse)
//                .toList();
//    }
//
//    /**
//     * Payment database entity ko Kafka event DTO
//     * mein convert karta hai.
//     */
//    private PaymentSuccessEvent createPaymentSuccessEvent(
//            Payment payment) {
//
//        String currency = null;
//
//        if (payment.getCurrency() != null) {
//            currency = payment.getCurrency().name();
//        }
//
//        return new PaymentSuccessEvent(
//                UUID.randomUUID(),
//                PAYMENT_SUCCESS_EVENT_TYPE,
//                payment.getPaymentId(),
//                payment.getUserId(),
//                payment.getPlanId(),
//                payment.getPlanName(),
//                payment.getRazorpayPaymentId(),
//                payment.getAmount(),
//                currency,
//                LocalDateTime.now()
//        );
//    }
//
//    /**
//     * Kafka event database transaction commit hone ke
//     * baad publish karta hai.
//     */
//    private void publishPaymentSuccessAfterCommit(
//            PaymentSuccessEvent event) {
//
//        /*
//         * Check karta hai ki current method active
//         * transaction ke andar execute ho raha hai.
//         */
//        if (TransactionSynchronizationManager
//                .isActualTransactionActive()) {
//
//            TransactionSynchronizationManager
//                    .registerSynchronization(
//                            new TransactionSynchronization() {
//
//                                /*
//                                 * Database transaction successfully
//                                 * commit hone ke baad execute hoga.
//                                 */
//                                @Override
//                                public void afterCommit() {
//
//                                    LOGGER.info(
//                                            "Payment transaction committed. "
//                                                    + "Publishing Kafka event. "
//                                                    + "paymentId={}, eventId={}",
//                                            event.getPaymentId(),
//                                            event.getEventId()
//                                    );
//
//                                    paymentEventProducer
//                                            .publishPaymentSuccess(
//                                                    event
//                                            );
//                                }
//                            }
//                    );
//
//            return;
//        }
//
//        /*
//         * Safety fallback:
//         * agar future mein method transaction ke bahar
//         * call hua to event immediately publish hoga.
//         */
//        LOGGER.warn(
//                "No active transaction found. Publishing payment event "
//                        + "immediately. paymentId={}, eventId={}",
//                event.getPaymentId(),
//                event.getEventId()
//        );
//
//        paymentEventProducer.publishPaymentSuccess(
//                event
//        );
//    }
//
//    /**
//     * SecurityContext se logged-in user ID nikalta hai.
//     */
//    private UUID getCurrentAuthenticatedUserId() {
//
//        Authentication authentication =
//                SecurityContextHolder
//                        .getContext()
//                        .getAuthentication();
//
//        if (authentication == null
//                || !(authentication.getPrincipal()
//                instanceof AuthenticatedUser authenticatedUser)) {
//
//            throw new PaymentProcessingException(
//                    "Unable to identify authenticated user."
//            );
//        }
//
//        return authenticatedUser.getUserId();
//    }
//
//    /**
//     * CodeCanvas ka unique internal receipt generate karta hai.
//     */
//    private String generateReceipt() {
//
//        return "CC-"
//                + UUID.randomUUID()
//                .toString()
//                .replace("-", "")
//                .substring(0, 10)
//                .toUpperCase();
//    }
//
//    /**
//     * Razorpay SDK ka use karke actual Razorpay order create karta hai.
//     */
//    private Order createRazorpayOrder(
//            SubscriptionPlanResponse plan,
//            String receipt)
//            throws RazorpayException {
//
//        JSONObject orderRequest =
//                new JSONObject();
//
//        orderRequest.put(
//                "amount",
//                convertToPaise(
//                        plan.getPrice()
//                )
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
//        return razorpayClient
//                .orders
//                .create(orderRequest);
//    }
//
//    /**
//     * Razorpay order ID, payment ID aur signature verify karta hai.
//     */
//    private boolean verifySignature(
//            VerifyPaymentRequest request) {
//
//        try {
//
//            JSONObject attributes =
//                    new JSONObject();
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
//        } catch (Exception exception) {
//
//            LOGGER.error(
//                    "Razorpay signature verification failed. orderId={}, error={}",
//                    request.getRazorpayOrderId(),
//                    exception.getMessage(),
//                    exception
//            );
//
//            return false;
//        }
//    }
//
//    /**
//     * Razorpay amount rupees mein nahi,
//     * smallest currency unit (paise) mein leta hai.
//     */
//    private long convertToPaise(
//            BigDecimal amount) {
//
//        return amount
//                .multiply(
//                        BigDecimal.valueOf(100)
//                )
//                .longValue();
//    }
//}

package com.codecanvas.paymentservice.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.codecanvas.paymentservice.dto.request.CreateOrderRequest;
import com.codecanvas.paymentservice.dto.request.MarkPaymentFailedRequest;
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
import com.codecanvas.paymentservice.kafka.event.PaymentFailedEvent;
import com.codecanvas.paymentservice.kafka.event.PaymentSuccessEvent;
import com.codecanvas.paymentservice.kafka.producer.PaymentEventProducer;
import com.codecanvas.paymentservice.mapper.PaymentMapper;
import com.codecanvas.paymentservice.repository.PaymentRepository;
import com.codecanvas.paymentservice.security.AuthenticatedUser;
import com.codecanvas.paymentservice.service.PaymentService;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;

@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(PaymentServiceImpl.class);

    private static final String PAYMENT_SUCCESS_EVENT_TYPE =
            "PAYMENT_SUCCESS";

    private static final String PAYMENT_FAILED_EVENT_TYPE =
            "PAYMENT_FAILED";

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final UserServiceClient userServiceClient;
    private final RazorpayClient razorpayClient;
    private final PaymentEventProducer paymentEventProducer;

    @Value("${razorpay.key-id}")
    private String razorpayKey;

    @Value("${razorpay.key-secret}")
    private String razorpaySecret;

    public PaymentServiceImpl(
            PaymentRepository paymentRepository,
            PaymentMapper paymentMapper,
            UserServiceClient userServiceClient,
            RazorpayClient razorpayClient,
            PaymentEventProducer paymentEventProducer) {

        this.paymentRepository = paymentRepository;
        this.paymentMapper = paymentMapper;
        this.userServiceClient = userServiceClient;
        this.razorpayClient = razorpayClient;
        this.paymentEventProducer = paymentEventProducer;
    }

    @Override
    public RazorpayOrderResponse createOrder(
            CreateOrderRequest request) {

        UUID userId =
                getCurrentAuthenticatedUserId();

        SubscriptionPlanResponse plan;

        try {

            /*
             * Payment Service ko plan ka price, name
             * aur currency User Service se milti hai.
             */
            plan = userServiceClient.getPlanById(
                    request.getPlanId()
            );

        } catch (Exception exception) {

            LOGGER.error(
                    "Unable to fetch subscription plan. "
                            + "planId={}, error={}",
                    request.getPlanId(),
                    exception.getMessage(),
                    exception
            );

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
                || plan.getPrice()
                .compareTo(BigDecimal.ZERO) <= 0) {

            throw new PaymentProcessingException(
                    "Free plans do not require Razorpay payment."
            );
        }

        String receipt =
                generateReceipt();

        /*
         * Razorpay order se pehle internal payment
         * record CREATED status ke saath save hota hai.
         */
        Payment payment =
                Payment.builder()
                        .userId(userId)
                        .planId(plan.getId())
                        .planName(plan.getName())
                        .amount(plan.getPrice())
                        .currency(plan.getCurrency())
                        .receipt(receipt)
                        .paymentStatus(
                                PaymentStatus.CREATED
                        )
                        .build();

        payment =
                paymentRepository.save(payment);

        try {

            Order razorpayOrder =
                    createRazorpayOrder(
                            plan,
                            receipt
                    );

            payment.setRazorpayOrderId(
                    razorpayOrder.get("id")
            );

            /*
             * Razorpay order bana hai, payment abhi
             * complete nahi hua.
             */
            payment.setPaymentStatus(
                    PaymentStatus.PENDING
            );

            payment =
                    paymentRepository.save(payment);

            return RazorpayOrderResponse
                    .builder()
                    .paymentId(
                            payment.getPaymentId()
                    )
                    .razorpayOrderId(
                            payment.getRazorpayOrderId()
                    )
                    .razorpayKey(
                            razorpayKey
                    )
                    .amount(
                            convertToPaise(
                                    plan.getPrice()
                            )
                    )
                    .currency(
                            plan.getCurrency().name()
                    )
                    .receipt(
                            receipt
                    )
                    .build();

        } catch (Exception exception) {

            LOGGER.error(
                    "Razorpay order creation failed. "
                            + "paymentId={}, error={}",
                    payment.getPaymentId(),
                    exception.getMessage(),
                    exception
            );

            payment.setPaymentStatus(
                    PaymentStatus.FAILED
            );

            payment.setFailureReason(
                    buildFailureReason(
                            "Razorpay order creation failed",
                            exception
                    )
            );

            paymentRepository.save(payment);

            throw new PaymentProcessingException(
                    "Failed to create Razorpay order: "
                            + exception.getMessage(),
                    exception
            );
        }
    }

    /**
     * Existing success flow remains unchanged.
     */
    @Override
    public PaymentResponse verifyPayment(
            VerifyPaymentRequest request) {

        Payment payment =
                paymentRepository
                        .findByRazorpayOrderId(
                                request.getRazorpayOrderId()
                        )
                        .orElseThrow(() ->
                                new PaymentNotFoundException(
                                        "Payment not found for the given Razorpay Order ID."
                                )
                        );

        /*
         * Same verify request dobara aaye to payment
         * aur Kafka event duplicate nahi honge.
         */
        if (payment.getPaymentStatus()
                == PaymentStatus.SUCCESS) {

            LOGGER.info(
                    "Payment already verified. "
                            + "Duplicate verification skipped. "
                            + "paymentId={}, razorpayOrderId={}",
                    payment.getPaymentId(),
                    payment.getRazorpayOrderId()
            );

            return paymentMapper.toResponse(
                    payment
            );
        }

        /*
         * Invalid signature ko actual Razorpay
         * payment failure nahi maana jayega.
         */
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

        payment.setFailureReason(null);

        payment.setWebhookProcessingStatus(
                WebhookProcessingStatus.PROCESSED
        );

        payment.setPaidAt(
                LocalDateTime.now()
        );

        Payment savedPayment =
                paymentRepository.save(payment);

        PaymentSuccessEvent paymentSuccessEvent =
                createPaymentSuccessEvent(
                        savedPayment
                );

        publishPaymentSuccessAfterCommit(
                paymentSuccessEvent
        );

        /*
         * Premium activation User Service ke
         * Kafka consumer se hogi.
         */
        return paymentMapper.toResponse(
                savedPayment
        );
    }

    /**
     * Frontend ke actual Razorpay payment.failed event
     * ko process karta hai.
     */
    @Override
    public PaymentResponse markPaymentFailed(
            MarkPaymentFailedRequest request) {

        Payment payment =
                paymentRepository
                        .findByRazorpayOrderId(
                                request.getRazorpayOrderId()
                        )
                        .orElseThrow(() ->
                                new PaymentNotFoundException(
                                        "Payment not found for the given Razorpay Order ID."
                                )
                        );

        UUID currentUserId =
                getCurrentAuthenticatedUserId();

        /*
         * User sirf apni payment report kar sakta hai.
         */
        if (!payment.getUserId()
                .equals(currentUserId)) {

            throw new PaymentNotFoundException(
                    "Payment not found."
            );
        }

        /*
         * Successful payment ko late frontend failure
         * callback se FAILED nahi banne dena.
         */
        if (payment.getPaymentStatus()
                == PaymentStatus.SUCCESS) {

            LOGGER.warn(
                    "Failed payment report ignored because "
                            + "payment is already successful. "
                            + "paymentId={}, razorpayOrderId={}",
                    payment.getPaymentId(),
                    payment.getRazorpayOrderId()
            );

            return paymentMapper.toResponse(
                    payment
            );
        }

        /*
         * Duplicate failure request par dobara
         * Kafka event publish nahi hoga.
         */
        if (payment.getPaymentStatus()
                == PaymentStatus.FAILED) {

            LOGGER.info(
                    "Payment already marked FAILED. "
                            + "Duplicate failure skipped. "
                            + "paymentId={}, razorpayOrderId={}",
                    payment.getPaymentId(),
                    payment.getRazorpayOrderId()
            );

            return paymentMapper.toResponse(
                    payment
            );
        }

        if (request.getRazorpayPaymentId() != null
                && !request.getRazorpayPaymentId()
                .isBlank()) {

            payment.setRazorpayPaymentId(
                    request.getRazorpayPaymentId()
            );
        }

        payment.setPaymentStatus(
                PaymentStatus.FAILED
        );

        payment.setFailureReason(
                request.getFailureReason()
        );

        Payment savedPayment =
                paymentRepository.save(payment);

        PaymentFailedEvent paymentFailedEvent =
                createPaymentFailedEvent(
                        savedPayment
                );

        publishPaymentFailedAfterCommit(
                paymentFailedEvent
        );

        LOGGER.warn(
                "Payment marked FAILED. "
                        + "Kafka event scheduled after commit. "
                        + "paymentId={}, razorpayOrderId={}, eventId={}",
                savedPayment.getPaymentId(),
                savedPayment.getRazorpayOrderId(),
                paymentFailedEvent.getEventId()
        );

        return paymentMapper.toResponse(
                savedPayment
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentById(
            UUID paymentId) {

        Payment payment =
                paymentRepository
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

        return paymentMapper.toResponse(
                payment
        );
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

    /**
     * SUCCESS entity ko Kafka DTO mein convert karta hai.
     */
    private PaymentSuccessEvent createPaymentSuccessEvent(
            Payment payment) {

        return new PaymentSuccessEvent(
                UUID.randomUUID(),
                PAYMENT_SUCCESS_EVENT_TYPE,
                payment.getPaymentId(),
                payment.getUserId(),
                payment.getPlanId(),
                payment.getPlanName(),
                payment.getRazorpayPaymentId(),
                payment.getAmount(),
                getCurrencyName(payment),
                LocalDateTime.now()
        );
    }

    /**
     * FAILED entity ko Kafka DTO mein convert karta hai.
     */
    private PaymentFailedEvent createPaymentFailedEvent(
            Payment payment) {

        return new PaymentFailedEvent(
                UUID.randomUUID(),
                PAYMENT_FAILED_EVENT_TYPE,
                payment.getPaymentId(),
                payment.getUserId(),
                payment.getPlanId(),
                payment.getPlanName(),
                payment.getRazorpayOrderId(),
                payment.getRazorpayPaymentId(),
                payment.getAmount(),
                getCurrencyName(payment),
                payment.getFailureReason(),
                LocalDateTime.now()
        );
    }

    /**
     * Success event DB transaction commit hone
     * ke baad Kafka par publish hota hai.
     */
    private void publishPaymentSuccessAfterCommit(
            PaymentSuccessEvent event) {

        if (TransactionSynchronizationManager
                .isActualTransactionActive()) {

            TransactionSynchronizationManager
                    .registerSynchronization(
                            new TransactionSynchronization() {

                                @Override
                                public void afterCommit() {

                                    LOGGER.info(
                                            "Payment transaction committed. "
                                                    + "Publishing PAYMENT_SUCCESS event. "
                                                    + "paymentId={}, eventId={}",
                                            event.getPaymentId(),
                                            event.getEventId()
                                    );

                                    paymentEventProducer
                                            .publishPaymentSuccess(
                                                    event
                                            );
                                }
                            }
                    );

            return;
        }

        LOGGER.warn(
                "No active transaction found. "
                        + "Publishing PAYMENT_SUCCESS immediately. "
                        + "paymentId={}, eventId={}",
                event.getPaymentId(),
                event.getEventId()
        );

        paymentEventProducer.publishPaymentSuccess(
                event
        );
    }

    /**
     * Failed event DB transaction commit hone
     * ke baad Kafka par publish hota hai.
     */
    private void publishPaymentFailedAfterCommit(
            PaymentFailedEvent event) {

        if (TransactionSynchronizationManager
                .isActualTransactionActive()) {

            TransactionSynchronizationManager
                    .registerSynchronization(
                            new TransactionSynchronization() {

                                @Override
                                public void afterCommit() {

                                    LOGGER.info(
                                            "Failed payment transaction committed. "
                                                    + "Publishing PAYMENT_FAILED event. "
                                                    + "paymentId={}, eventId={}",
                                            event.getPaymentId(),
                                            event.getEventId()
                                    );

                                    paymentEventProducer
                                            .publishPaymentFailed(
                                                    event
                                            );
                                }
                            }
                    );

            return;
        }

        LOGGER.warn(
                "No active transaction found. "
                        + "Publishing PAYMENT_FAILED immediately. "
                        + "paymentId={}, eventId={}",
                event.getPaymentId(),
                event.getEventId()
        );

        paymentEventProducer.publishPaymentFailed(
                event
        );
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
                convertToPaise(
                        plan.getPrice()
                )
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
                    "Razorpay signature verification failed. "
                            + "orderId={}, error={}",
                    request.getRazorpayOrderId(),
                    exception.getMessage(),
                    exception
            );

            return false;
        }
    }

    private String getCurrencyName(
            Payment payment) {

        if (payment.getCurrency() == null) {
            return null;
        }

        return payment
                .getCurrency()
                .name();
    }

    private String buildFailureReason(
            String prefix,
            Exception exception) {

        if (exception.getMessage() == null
                || exception.getMessage()
                .isBlank()) {

            return prefix;
        }

        return prefix
                + ": "
                + exception.getMessage();
    }

    private long convertToPaise(
            BigDecimal amount) {

        return amount
                .multiply(
                        BigDecimal.valueOf(100)
                )
                .longValue();
    }
}