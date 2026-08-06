//package com.codecanvas.userservice.listener;
//
//import com.codecanvas.userservice.service.SubscriptionService;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.stereotype.Component;
//
//import java.math.BigDecimal;
//import java.util.UUID;
//
//@Component
//@RequiredArgsConstructor
//@Slf4j
//public class PaymentEventListener {
//
//    private final SubscriptionService subscriptionService;
//    private final ObjectMapper objectMapper;
//
//    /**
//     * Handle payment success event from Payment Service
//     */
//    @KafkaListener(topics = "payment.success", groupId = "user-service")
//    public void handlePaymentSuccess(String message) {
//        try {
//            PaymentSuccessEvent event = objectMapper.readValue(message, PaymentSuccessEvent.class);
//
//            subscriptionService.createSubscription(
//                    event.getUserId(),
//                    event.getPlanId(),
//                    event.getPaymentId(),
//                    event.getPaymentMethod()
//            );
//
//            log.info("Subscription successfully created after payment success for user {}", event.getUserId());
//        } catch (Exception e) {
//            log.error("Error processing payment success event", e);
//        }
//    }
//
//    /**
//     * Handle payment failure event
//     */
//    @KafkaListener(topics = "payment.failed", groupId = "user-service")
//    public void handlePaymentFailure(String message) {
//        try {
//            PaymentFailureEvent event = objectMapper.readValue(message, PaymentFailureEvent.class);
//            log.warn("Payment failed for user {}: {}", event.getUserId(), event.getFailureReason());
//        } catch (Exception e) {
//            log.error("Error processing payment failure event", e);
//        }
//    }
//
//    @lombok.Data
//    @lombok.NoArgsConstructor
//    @lombok.AllArgsConstructor
//    public static class PaymentSuccessEvent {
//        private UUID userId;
//        private Long planId;
//        private String paymentId;
//        private String paymentMethod;
//        private BigDecimal amount;
//    }
//
//    @lombok.Data
//    @lombok.NoArgsConstructor
//    @lombok.AllArgsConstructor
//    public static class PaymentFailureEvent {
//        private UUID userId;
//        private Long planId;
//        private String paymentId;
//        private String failureReason;
//    }
//}
