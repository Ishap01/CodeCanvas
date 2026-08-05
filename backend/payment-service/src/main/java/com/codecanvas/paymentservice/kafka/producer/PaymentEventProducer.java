package com.codecanvas.paymentservice.kafka.producer;

import com.codecanvas.paymentservice.kafka.event.PaymentFailedEvent;
import com.codecanvas.paymentservice.kafka.event.PaymentSuccessEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Component
public class PaymentEventProducer {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(PaymentEventProducer.class);

    /*
     * Object use kiya hai kyunki same KafkaTemplate se
     * PaymentSuccessEvent aur PaymentFailedEvent dono publish honge.
     */
    private final KafkaTemplate<String, Object> kafkaTemplate;

    private final String paymentSuccessTopic;
    private final String paymentFailedTopic;

    public PaymentEventProducer(
            KafkaTemplate<String, Object> kafkaTemplate,

            @Value("${app.kafka.topics.payment-success}")
            String paymentSuccessTopic,

            @Value("${app.kafka.topics.payment-failed}")
            String paymentFailedTopic) {

        this.kafkaTemplate = kafkaTemplate;
        this.paymentSuccessTopic = paymentSuccessTopic;
        this.paymentFailedTopic = paymentFailedTopic;
    }

    /**
     * Successful payment ka event Kafka par publish karta hai.
     */
    public void publishPaymentSuccess(
            PaymentSuccessEvent event) {

        validatePaymentSuccessEvent(event);

        publishEvent(
                paymentSuccessTopic,
                event.getPaymentId(),
                event.getEventId(),
                "PAYMENT_SUCCESS",
                event
        );
    }

    /**
     * Failed payment ka event Kafka par publish karta hai.
     */
    public void publishPaymentFailed(
            PaymentFailedEvent event) {

        validatePaymentFailedEvent(event);

        publishEvent(
                paymentFailedTopic,
                event.getPaymentId(),
                event.getEventId(),
                "PAYMENT_FAILED",
                event
        );
    }

    /**
     * Common Kafka publishing method.
     *
     * Success aur failed dono events ke liye duplicate
     * KafkaTemplate send code likhne ki zarurat nahi padti.
     */
    private void publishEvent(
            String topic,
            UUID paymentId,
            UUID eventId,
            String eventType,
            Object event) {

        String messageKey =
                paymentId.toString();

        LOGGER.info(
                "Publishing {} event to Kafka. "
                        + "topic={}, paymentId={}, eventId={}",
                eventType,
                topic,
                paymentId,
                eventId
        );

        CompletableFuture<SendResult<String, Object>> future =
                kafkaTemplate.send(
                        topic,
                        messageKey,
                        event
                );

        future.whenComplete((result, exception) -> {

            if (exception != null) {

                LOGGER.error(
                        "Failed to publish {} event. "
                                + "topic={}, paymentId={}, eventId={}, error={}",
                        eventType,
                        topic,
                        paymentId,
                        eventId,
                        exception.getMessage(),
                        exception
                );

                return;
            }

            LOGGER.info(
                    "{} event published successfully. "
                            + "topic={}, partition={}, offset={}, "
                            + "paymentId={}, eventId={}",
                    eventType,
                    result.getRecordMetadata().topic(),
                    result.getRecordMetadata().partition(),
                    result.getRecordMetadata().offset(),
                    paymentId,
                    eventId
            );
        });
    }

    /**
     * Payment success event ke mandatory fields validate karta hai.
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
                    "Razorpay payment ID is required for payment success event."
            );
        }
    }

    /**
     * Payment failed event ke mandatory fields validate karta hai.
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

        if (event.getFailureReason() == null
                || event.getFailureReason().isBlank()) {

            throw new IllegalArgumentException(
                    "Failure reason is required for payment failed event."
            );
        }
    }

    /**
     * Success aur failed events ke common fields validate karta hai.
     */
    private void validateCommonFields(
            UUID eventId,
            UUID paymentId,
            UUID userId,
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