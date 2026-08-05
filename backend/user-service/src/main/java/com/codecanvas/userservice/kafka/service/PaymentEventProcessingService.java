//package com.codecanvas.userservice.kafka.service;
//
//import com.codecanvas.userservice.entity.ProcessedKafkaEvent;
//import com.codecanvas.userservice.kafka.event.PaymentSuccessEvent;
//import com.codecanvas.userservice.repository.ProcessedKafkaEventRepository;
//import com.codecanvas.userservice.repository.UserSubscriptionRepository;
//import com.codecanvas.userservice.service.SubscriptionService;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDateTime;
//
//@Service
//public class PaymentEventProcessingService {
//
//    private static final Logger LOGGER =
//            LoggerFactory.getLogger(
//                    PaymentEventProcessingService.class
//            );
//
//    private static final String PAYMENT_SUCCESS =
//            "PAYMENT_SUCCESS";
//
//    private static final String PAYMENT_METHOD =
//            "RAZORPAY";
//
//    private final ProcessedKafkaEventRepository
//            processedKafkaEventRepository;
//
//    private final UserSubscriptionRepository
//            userSubscriptionRepository;
//
//    private final SubscriptionService
//            subscriptionService;
//
//    public PaymentEventProcessingService(
//            ProcessedKafkaEventRepository processedKafkaEventRepository,
//            UserSubscriptionRepository userSubscriptionRepository,
//            SubscriptionService subscriptionService) {
//
//        this.processedKafkaEventRepository =
//                processedKafkaEventRepository;
//
//        this.userSubscriptionRepository =
//                userSubscriptionRepository;
//
//        this.subscriptionService =
//                subscriptionService;
//    }
//
//    @Transactional
//    public void processPaymentSuccessEvent(
//            PaymentSuccessEvent event) {
//
//        validateEvent(event);
//
//        /*
//         * Check 1:
//         * Same exact Kafka event pehle process hua hai?
//         */
//        if (processedKafkaEventRepository
//                .existsById(event.getEventId())) {
//
//            LOGGER.warn(
//                    "Kafka event already processed. "
//                            + "Skipping eventId={}, paymentId={}",
//                    event.getEventId(),
//                    event.getPaymentId()
//            );
//
//            return;
//        }
//
//        /*
//         * Check 2:
//         * Same CodeCanvas payment ID pehle Kafka se process hua hai?
//         */
//        if (processedKafkaEventRepository
//                .existsByPaymentId(event.getPaymentId())) {
//
//            LOGGER.warn(
//                    "Payment event already processed. "
//                            + "Skipping eventId={}, paymentId={}",
//                    event.getEventId(),
//                    event.getPaymentId()
//            );
//
//            return;
//        }
//
//        /*
//         * Check 3:
//         * Purane Feign flow ne isi Razorpay payment se
//         * subscription already create ki hai?
//         */
//        if (userSubscriptionRepository.existsByPaymentId(
//                event.getRazorpayPaymentId()
//        )) {
//
//            LOGGER.warn(
//                    "Subscription already exists for Razorpay payment. "
//                            + "Kafka subscription creation skipped. "
//                            + "eventId={}, razorpayPaymentId={}",
//                    event.getEventId(),
//                    event.getRazorpayPaymentId()
//            );
//
//            saveProcessedEvent(event);
//
//            return;
//        }
//
//        /*
//         * Existing subscription business method reuse hoga.
//         */
//        subscriptionService.createSubscription(
//                event.getUserId(),
//                event.getPlanId(),
//                event.getRazorpayPaymentId(),
//                PAYMENT_METHOD
//        );
//
//        /*
//         * Subscription successfully create hone ke baad event
//         * processed mark hoga.
//         */
//        saveProcessedEvent(event);
//
//        LOGGER.info(
//                "PAYMENT_SUCCESS processed successfully. "
//                        + "eventId={}, paymentId={}, userId={}, planId={}",
//                event.getEventId(),
//                event.getPaymentId(),
//                event.getUserId(),
//                event.getPlanId()
//        );
//    }
//
//    private void saveProcessedEvent(
//            PaymentSuccessEvent event) {
//
//        ProcessedKafkaEvent processedEvent =
//                new ProcessedKafkaEvent(
//                        event.getEventId(),
//                        event.getEventType(),
//                        event.getPaymentId(),
//                        LocalDateTime.now()
//                );
//
//        processedKafkaEventRepository.save(
//                processedEvent
//        );
//    }
//
//    private void validateEvent(
//            PaymentSuccessEvent event) {
//
//        if (event == null) {
//            throw new IllegalArgumentException(
//                    "Payment success event cannot be null."
//            );
//        }
//
//        if (event.getEventId() == null) {
//            throw new IllegalArgumentException(
//                    "Event ID is required."
//            );
//        }
//
//        if (event.getPaymentId() == null) {
//            throw new IllegalArgumentException(
//                    "Payment ID is required."
//            );
//        }
//
//        if (event.getUserId() == null) {
//            throw new IllegalArgumentException(
//                    "User ID is required."
//            );
//        }
//
//        if (event.getPlanId() == null) {
//            throw new IllegalArgumentException(
//                    "Plan ID is required."
//            );
//        }
//
//        if (event.getRazorpayPaymentId() == null
//                || event.getRazorpayPaymentId().isBlank()) {
//
//            throw new IllegalArgumentException(
//                    "Razorpay payment ID is required."
//            );
//        }
//
//        if (!PAYMENT_SUCCESS.equals(
//                event.getEventType()
//        )) {
//
//            throw new IllegalArgumentException(
//                    "Unsupported payment event type: "
//                            + event.getEventType()
//            );
//        }
//    }
//}

package com.codecanvas.userservice.kafka.service;

import com.codecanvas.userservice.entity.ProcessedKafkaEvent;
import com.codecanvas.userservice.kafka.event.PaymentFailedEvent;
import com.codecanvas.userservice.kafka.event.PaymentSuccessEvent;
import com.codecanvas.userservice.repository.ProcessedKafkaEventRepository;
import com.codecanvas.userservice.repository.UserSubscriptionRepository;
import com.codecanvas.userservice.service.SubscriptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class PaymentEventProcessingService {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(
                    PaymentEventProcessingService.class
            );

    private static final String PAYMENT_SUCCESS =
            "PAYMENT_SUCCESS";

    private static final String PAYMENT_FAILED =
            "PAYMENT_FAILED";

    private static final String PAYMENT_METHOD =
            "RAZORPAY";

    private final ProcessedKafkaEventRepository
            processedKafkaEventRepository;

    private final UserSubscriptionRepository
            userSubscriptionRepository;

    private final SubscriptionService
            subscriptionService;

    public PaymentEventProcessingService(
            ProcessedKafkaEventRepository processedKafkaEventRepository,
            UserSubscriptionRepository userSubscriptionRepository,
            SubscriptionService subscriptionService) {

        this.processedKafkaEventRepository =
                processedKafkaEventRepository;

        this.userSubscriptionRepository =
                userSubscriptionRepository;

        this.subscriptionService =
                subscriptionService;
    }

    /**
     * Successful payment process karta hai aur
     * premium subscription create karta hai.
     */
    @Transactional
    public void processPaymentSuccessEvent(
            PaymentSuccessEvent event) {

        validatePaymentSuccessEvent(event);

        /*
         * Same exact Kafka event dobara process nahi hoga.
         */
        if (processedKafkaEventRepository
                .existsById(event.getEventId())) {

            LOGGER.warn(
                    "Kafka event already processed. "
                            + "Skipping PAYMENT_SUCCESS eventId={}, paymentId={}",
                    event.getEventId(),
                    event.getPaymentId()
            );

            return;
        }

        /*
         * Same CodeCanvas payment success event dobara
         * process nahi hoga.
         */
        if (processedKafkaEventRepository
                .existsByPaymentId(event.getPaymentId())) {

            LOGGER.warn(
                    "Payment event already processed. "
                            + "Skipping PAYMENT_SUCCESS eventId={}, paymentId={}",
                    event.getEventId(),
                    event.getPaymentId()
            );

            return;
        }

        /*
         * Purane Feign flow ya previous Kafka processing ne
         * subscription already create ki hai to duplicate skip hoga.
         */
        if (userSubscriptionRepository.existsByPaymentId(
                event.getRazorpayPaymentId()
        )) {

            LOGGER.warn(
                    "Subscription already exists for Razorpay payment. "
                            + "Kafka subscription creation skipped. "
                            + "eventId={}, razorpayPaymentId={}",
                    event.getEventId(),
                    event.getRazorpayPaymentId()
            );

            saveProcessedSuccessEvent(event);

            return;
        }

        subscriptionService.createSubscription(
                event.getUserId(),
                event.getPlanId(),
                event.getRazorpayPaymentId(),
                PAYMENT_METHOD
        );

        saveProcessedSuccessEvent(event);

        LOGGER.info(
                "PAYMENT_SUCCESS processed successfully. "
                        + "eventId={}, paymentId={}, userId={}, planId={}",
                event.getEventId(),
                event.getPaymentId(),
                event.getUserId(),
                event.getPlanId()
        );
    }

    /**
     * Failed payment event process karta hai.
     *
     * Failed payment par subscription activate nahi hogi.
     * Event ko sirf validated aur processed mark kiya jayega.
     */
    @Transactional
    public void processPaymentFailedEvent(
            PaymentFailedEvent event) {

        validatePaymentFailedEvent(event);

        /*
         * Same exact failed Kafka event dobara process nahi hoga.
         */
        if (processedKafkaEventRepository
                .existsById(event.getEventId())) {

            LOGGER.warn(
                    "Kafka event already processed. "
                            + "Skipping PAYMENT_FAILED eventId={}, paymentId={}",
                    event.getEventId(),
                    event.getPaymentId()
            );

            return;
        }

        /*
         * Same failed payment event dobara process nahi hoga.
         */
        if (processedKafkaEventRepository
                .existsByPaymentId(event.getPaymentId())) {

            LOGGER.warn(
                    "Payment event already processed. "
                            + "Skipping PAYMENT_FAILED eventId={}, paymentId={}",
                    event.getEventId(),
                    event.getPaymentId()
            );

            return;
        }

        /*
         * Failed payment ke liye subscriptionService call
         * intentionally nahi ki ja rahi.
         */
        saveProcessedFailedEvent(event);

        LOGGER.warn(
                "PAYMENT_FAILED processed successfully. "
                        + "No subscription was activated. "
                        + "eventId={}, paymentId={}, userId={}, "
                        + "planId={}, reason={}",
                event.getEventId(),
                event.getPaymentId(),
                event.getUserId(),
                event.getPlanId(),
                event.getFailureReason()
        );
    }

    /**
     * Success event ko processed table mein save karta hai.
     */
    private void saveProcessedSuccessEvent(
            PaymentSuccessEvent event) {

        ProcessedKafkaEvent processedEvent =
                new ProcessedKafkaEvent(
                        event.getEventId(),
                        event.getEventType(),
                        event.getPaymentId(),
                        LocalDateTime.now()
                );

        processedKafkaEventRepository.save(
                processedEvent
        );
    }

    /**
     * Failed event ko processed table mein save karta hai.
     */
    private void saveProcessedFailedEvent(
            PaymentFailedEvent event) {

        ProcessedKafkaEvent processedEvent =
                new ProcessedKafkaEvent(
                        event.getEventId(),
                        event.getEventType(),
                        event.getPaymentId(),
                        LocalDateTime.now()
                );

        processedKafkaEventRepository.save(
                processedEvent
        );
    }

    /**
     * PAYMENT_SUCCESS event fields validate karta hai.
     */
    private void validatePaymentSuccessEvent(
            PaymentSuccessEvent event) {

        if (event == null) {
            throw new IllegalArgumentException(
                    "Payment success event cannot be null."
            );
        }

        validateCommonFields(
                event.getEventId(),
                event.getPaymentId(),
                event.getUserId(),
                event.getPlanId()
        );

        if (event.getRazorpayPaymentId() == null
                || event.getRazorpayPaymentId().isBlank()) {

            throw new IllegalArgumentException(
                    "Razorpay payment ID is required."
            );
        }

        if (!PAYMENT_SUCCESS.equals(
                event.getEventType()
        )) {

            throw new IllegalArgumentException(
                    "Unsupported payment success event type: "
                            + event.getEventType()
            );
        }
    }

    /**
     * PAYMENT_FAILED event fields validate karta hai.
     */
    private void validatePaymentFailedEvent(
            PaymentFailedEvent event) {

        if (event == null) {
            throw new IllegalArgumentException(
                    "Payment failed event cannot be null."
            );
        }

        validateCommonFields(
                event.getEventId(),
                event.getPaymentId(),
                event.getUserId(),
                event.getPlanId()
        );

        if (event.getRazorpayOrderId() == null
                || event.getRazorpayOrderId().isBlank()) {

            throw new IllegalArgumentException(
                    "Razorpay order ID is required."
            );
        }

        if (event.getFailureReason() == null
                || event.getFailureReason().isBlank()) {

            throw new IllegalArgumentException(
                    "Payment failure reason is required."
            );
        }

        if (!PAYMENT_FAILED.equals(
                event.getEventType()
        )) {

            throw new IllegalArgumentException(
                    "Unsupported payment failed event type: "
                            + event.getEventType()
            );
        }
    }

    /**
     * Success aur failed event ke common fields validate karta hai.
     */
    private void validateCommonFields(
            java.util.UUID eventId,
            java.util.UUID paymentId,
            java.util.UUID userId,
            Long planId) {

        if (eventId == null) {
            throw new IllegalArgumentException(
                    "Event ID is required."
            );
        }

        if (paymentId == null) {
            throw new IllegalArgumentException(
                    "Payment ID is required."
            );
        }

        if (userId == null) {
            throw new IllegalArgumentException(
                    "User ID is required."
            );
        }

        if (planId == null) {
            throw new IllegalArgumentException(
                    "Plan ID is required."
            );
        }
    }
}