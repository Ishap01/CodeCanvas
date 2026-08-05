//package com.codecanvas.userservice.kafka.consumer;
//
//import com.codecanvas.userservice.kafka.event.PaymentSuccessEvent;
//import com.codecanvas.userservice.kafka.service.PaymentEventProcessingService;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.stereotype.Component;
//
//@Component
//public class PaymentEventConsumer {
//
//    private static final Logger LOGGER =
//            LoggerFactory.getLogger(
//                    PaymentEventConsumer.class
//            );
//
//    private final PaymentEventProcessingService
//            paymentEventProcessingService;
//
//    public PaymentEventConsumer(
//            PaymentEventProcessingService
//                    paymentEventProcessingService) {
//
//        this.paymentEventProcessingService =
//                paymentEventProcessingService;
//    }
//
//    @KafkaListener(
//            topics = "${app.kafka.topics.payment-success}",
//            groupId = "${spring.kafka.consumer.group-id}"
//    )
//    public void consumePaymentSuccessEvent(
//            PaymentSuccessEvent event) {
//
//        LOGGER.info(
//                "PAYMENT_SUCCESS event received from Kafka. "
//                        + "eventId={}, paymentId={}, userId={}, planId={}",
//                event.getEventId(),
//                event.getPaymentId(),
//                event.getUserId(),
//                event.getPlanId()
//        );
//
//        /*
//         * Event processing service validation,
//         * duplicate checking aur subscription activation karegi.
//         *
//         * Exception aayi to listener successful complete nahi hoga.
//         * Kafka event ko retry/redeliver kar sakta hai.
//         */
//        paymentEventProcessingService
//                .processPaymentSuccessEvent(event);
//
//        LOGGER.info(
//                "PAYMENT_SUCCESS Kafka listener completed. "
//                        + "eventId={}, paymentId={}",
//                event.getEventId(),
//                event.getPaymentId()
//        );
//    }
//}


package com.codecanvas.userservice.kafka.consumer;

import com.codecanvas.userservice.kafka.event.PaymentFailedEvent;
import com.codecanvas.userservice.kafka.event.PaymentSuccessEvent;
import com.codecanvas.userservice.kafka.service.PaymentEventProcessingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentEventConsumer {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(
                    PaymentEventConsumer.class
            );

    private final PaymentEventProcessingService
            paymentEventProcessingService;

    public PaymentEventConsumer(
            PaymentEventProcessingService paymentEventProcessingService) {

        this.paymentEventProcessingService =
                paymentEventProcessingService;
    }

    /**
     * Successful payment event consume karta hai.
     *
     * PAYMENT_SUCCESS receive hone par User Service
     * premium subscription activate karegi.
     */
    @KafkaListener(
            topics = "${app.kafka.topics.payment-success}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumePaymentSuccessEvent(
            PaymentSuccessEvent event) {

        LOGGER.info(
                "PAYMENT_SUCCESS event received from Kafka. "
                        + "eventId={}, paymentId={}, userId={}, planId={}",
                event.getEventId(),
                event.getPaymentId(),
                event.getUserId(),
                event.getPlanId()
        );

        paymentEventProcessingService
                .processPaymentSuccessEvent(event);

        LOGGER.info(
                "PAYMENT_SUCCESS Kafka listener completed. "
                        + "eventId={}, paymentId={}",
                event.getEventId(),
                event.getPaymentId()
        );
    }

    /**
     * Failed payment event consume karta hai.
     *
     * PAYMENT_FAILED receive hone par subscription
     * activate nahi hogi. Event sirf process aur
     * duplicate-protection table mein record hoga.
     */
    @KafkaListener(
            topics = "${app.kafka.topics.payment-failed}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory =
                    "paymentFailedKafkaListenerContainerFactory"
    )
    public void consumePaymentFailedEvent(
            PaymentFailedEvent event) {

        LOGGER.warn(
                "PAYMENT_FAILED event received from Kafka. "
                        + "eventId={}, paymentId={}, userId={}, "
                        + "planId={}, razorpayOrderId={}, failureReason={}",
                event.getEventId(),
                event.getPaymentId(),
                event.getUserId(),
                event.getPlanId(),
                event.getRazorpayOrderId(),
                event.getFailureReason()
        );

        paymentEventProcessingService
                .processPaymentFailedEvent(event);

        LOGGER.info(
                "PAYMENT_FAILED Kafka listener completed. "
                        + "eventId={}, paymentId={}",
                event.getEventId(),
                event.getPaymentId()
        );
    }
}