package com.codecanvas.paymentservice.service.impl;

import java.time.LocalDateTime;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codecanvas.paymentservice.entity.Payment;
import com.codecanvas.paymentservice.entity.WebhookEvent;
import com.codecanvas.paymentservice.enums.PaymentStatus;
import com.codecanvas.paymentservice.enums.WebhookProcessingStatus;
import com.codecanvas.paymentservice.repository.PaymentRepository;
import com.codecanvas.paymentservice.repository.WebhookEventRepository;
import com.codecanvas.paymentservice.service.RazorpayService;
import com.codecanvas.paymentservice.service.WebhookService;

@Service
public class WebhookServiceImpl implements WebhookService {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(WebhookServiceImpl.class);

    private static final int MAXIMUM_FAILURE_REASON_LENGTH = 1000;

    private final RazorpayService razorpayService;
    private final WebhookEventRepository webhookEventRepository;
    private final PaymentRepository paymentRepository;

    public WebhookServiceImpl(
            RazorpayService razorpayService,
            WebhookEventRepository webhookEventRepository,
            PaymentRepository paymentRepository) {

        this.razorpayService = razorpayService;
        this.webhookEventRepository = webhookEventRepository;
        this.paymentRepository = paymentRepository;
    }

    @Override
    @Transactional
    public void processRazorpayWebhook(
            String payload,
            String webhookSignature,
            String razorpayEventId) {

        validateWebhookRequest(
                payload,
                webhookSignature,
                razorpayEventId
        );

        boolean signatureValid =
                razorpayService.verifyWebhookSignature(
                        payload,
                        webhookSignature
                );

        if (!signatureValid) {
            throw new SecurityException(
                    "Invalid Razorpay webhook signature."
            );
        }

        if (webhookEventRepository
                .existsByRazorpayEventId(razorpayEventId)) {

            LOGGER.info(
                    "Duplicate Razorpay webhook ignored. razorpayEventId={}",
                    razorpayEventId
            );

            return;
        }

        WebhookEvent webhookEvent =
                createWebhookEvent(
                        payload,
                        razorpayEventId
                );

        try {
            JSONObject jsonPayload =
                    new JSONObject(payload);

            String eventType =
                    jsonPayload.getString("event");

            webhookEvent.setEventType(eventType);

            webhookEventRepository.save(webhookEvent);

            processEvent(
                    eventType,
                    jsonPayload
            );

            webhookEvent.setStatus(
                    WebhookProcessingStatus.PROCESSED
            );

            webhookEvent.setProcessedAt(
                    LocalDateTime.now()
            );

            webhookEvent.setFailureReason(null);

            webhookEventRepository.save(webhookEvent);

            LOGGER.info(
                    "Razorpay webhook processed successfully. "
                            + "razorpayEventId={}, eventType={}",
                    razorpayEventId,
                    eventType
            );

        } catch (Exception exception) {

            webhookEvent.setStatus(
                    WebhookProcessingStatus.FAILED
            );

            webhookEvent.setFailureReason(
                    limitFailureReason(
                            exception.getMessage()
                    )
            );

            webhookEvent.setProcessedAt(
                    LocalDateTime.now()
            );

            webhookEventRepository.save(webhookEvent);

            LOGGER.error(
                    "Razorpay webhook processing failed. "
                            + "razorpayEventId={}",
                    razorpayEventId,
                    exception
            );

            throw new IllegalStateException(
                    "Unable to process Razorpay webhook.",
                    exception
            );
        }
    }

    private WebhookEvent createWebhookEvent(
            String payload,
            String razorpayEventId) {

        WebhookEvent webhookEvent =
                new WebhookEvent();

        webhookEvent.setRazorpayEventId(
                razorpayEventId
        );

        webhookEvent.setEventType(
                "UNKNOWN"
        );

        webhookEvent.setPayload(
                payload
        );

        webhookEvent.setStatus(
                WebhookProcessingStatus.PROCESSING
        );

        return webhookEventRepository.save(
                webhookEvent
        );
    }

    private void processEvent(
            String eventType,
            JSONObject jsonPayload) {

        switch (eventType) {

            case "payment.authorized" ->
                    handlePaymentAuthorized(
                            jsonPayload
                    );

            case "payment.captured",
                 "order.paid" ->
                    handlePaymentCaptured(
                            jsonPayload
                    );

            case "payment.failed" ->
                    handlePaymentFailed(
                            jsonPayload
                    );

            default ->
                    LOGGER.info(
                            "Webhook event ignored because no handler is configured. "
                                    + "eventType={}",
                            eventType
                    );
        }
    }

    private void handlePaymentAuthorized(
            JSONObject jsonPayload) {

        JSONObject paymentEntity =
                extractPaymentEntity(
                        jsonPayload
                );

        String orderId =
                paymentEntity.getString(
                        "order_id"
                );

        String razorpayPaymentId =
                paymentEntity.getString(
                        "id"
                );

        paymentRepository
                .findByRazorpayOrderId(orderId)
                .ifPresentOrElse(
                        payment -> {

                            payment.setRazorpayPaymentId(
                                    razorpayPaymentId
                            );

                            /*
                             * SUCCESS payment ko webhook ke late arrival
                             * ke karan AUTHORIZED mein downgrade nahi karna.
                             */
                            if (payment.getPaymentStatus()
                                    != PaymentStatus.SUCCESS) {

                                payment.setPaymentStatus(
                                        PaymentStatus.AUTHORIZED
                                );
                            }

                            payment.setFailureReason(null);

                            paymentRepository.save(payment);

                            LOGGER.info(
                                    "Payment authorized through webhook. "
                                            + "paymentId={}, razorpayOrderId={}",
                                    payment.getPaymentId(),
                                    orderId
                            );
                        },
                        () -> LOGGER.warn(
                                "Payment not found for payment.authorized webhook. "
                                        + "razorpayOrderId={}",
                                orderId
                        )
                );
    }

    private void handlePaymentCaptured(
            JSONObject jsonPayload) {

        JSONObject paymentEntity =
                extractPaymentEntity(
                        jsonPayload
                );

        String orderId =
                paymentEntity.getString(
                        "order_id"
                );

        String razorpayPaymentId =
                paymentEntity.getString(
                        "id"
                );

        paymentRepository
                .findByRazorpayOrderId(orderId)
                .ifPresentOrElse(
                        payment -> {

                            payment.setRazorpayPaymentId(
                                    razorpayPaymentId
                            );

                            /*
                             * Frontend verify API already SUCCESS kar chuki ho
                             * to webhook us status ko CAPTURED mein downgrade
                             * nahi karega.
                             */
                            if (payment.getPaymentStatus()
                                    != PaymentStatus.SUCCESS) {

                                payment.setPaymentStatus(
                                        PaymentStatus.CAPTURED
                                );
                            }

                            payment.setFailureReason(null);

                            if (payment.getPaidAt() == null) {
                                payment.setPaidAt(
                                        LocalDateTime.now()
                                );
                            }

                            paymentRepository.save(payment);

                            LOGGER.info(
                                    "Payment captured through webhook. "
                                            + "paymentId={}, razorpayOrderId={}, "
                                            + "razorpayPaymentId={}",
                                    payment.getPaymentId(),
                                    orderId,
                                    razorpayPaymentId
                            );
                        },
                        () -> LOGGER.warn(
                                "Payment not found for captured webhook. "
                                        + "razorpayOrderId={}",
                                orderId
                        )
                );
    }

    private void handlePaymentFailed(
            JSONObject jsonPayload) {

        JSONObject paymentEntity =
                extractPaymentEntity(
                        jsonPayload
                );

        String orderId =
                paymentEntity.getString(
                        "order_id"
                );

        String razorpayPaymentId =
                paymentEntity.optString(
                        "id",
                        null
                );

        String failureReason =
                extractFailureReason(
                        paymentEntity
                );

        paymentRepository
                .findByRazorpayOrderId(orderId)
                .ifPresentOrElse(
                        payment -> {

                            /*
                             * Successful payment ko late/out-of-order failed
                             * webhook se FAILED nahi banana.
                             */
                            if (payment.getPaymentStatus()
                                    == PaymentStatus.SUCCESS
                                    || payment.getPaymentStatus()
                                    == PaymentStatus.CAPTURED) {

                                LOGGER.warn(
                                        "Failed webhook ignored because payment "
                                                + "is already successful. "
                                                + "paymentId={}, razorpayOrderId={}",
                                        payment.getPaymentId(),
                                        orderId
                                );

                                return;
                            }

                            if (razorpayPaymentId != null
                                    && !razorpayPaymentId.isBlank()) {

                                payment.setRazorpayPaymentId(
                                        razorpayPaymentId
                                );
                            }

                            payment.setPaymentStatus(
                                    PaymentStatus.FAILED
                            );

                            payment.setFailureReason(
                                    failureReason
                            );

                            paymentRepository.save(payment);

                            LOGGER.info(
                                    "Payment marked FAILED through webhook. "
                                            + "paymentId={}, razorpayOrderId={}",
                                    payment.getPaymentId(),
                                    orderId
                            );
                        },
                        () -> LOGGER.warn(
                                "Payment not found for payment.failed webhook. "
                                        + "razorpayOrderId={}",
                                orderId
                        )
                );
    }

    private JSONObject extractPaymentEntity(
            JSONObject jsonPayload) {

        return jsonPayload
                .getJSONObject("payload")
                .getJSONObject("payment")
                .getJSONObject("entity");
    }

    private String extractFailureReason(
            JSONObject paymentEntity) {

        String description =
                paymentEntity.optString(
                        "error_description",
                        null
                );

        if (description != null
                && !description.isBlank()) {

            return description;
        }

        String reason =
                paymentEntity.optString(
                        "error_reason",
                        null
                );

        if (reason != null
                && !reason.isBlank()) {

            return reason;
        }

        String code =
                paymentEntity.optString(
                        "error_code",
                        null
                );

        if (code != null
                && !code.isBlank()) {

            return code;
        }

        return "Razorpay marked payment as failed.";
    }

    private void validateWebhookRequest(
            String payload,
            String webhookSignature,
            String razorpayEventId) {

        if (payload == null
                || payload.isBlank()) {

            throw new IllegalArgumentException(
                    "Webhook payload is required."
            );
        }

        if (webhookSignature == null
                || webhookSignature.isBlank()) {

            throw new IllegalArgumentException(
                    "Razorpay webhook signature is required."
            );
        }

        if (razorpayEventId == null
                || razorpayEventId.isBlank()) {

            throw new IllegalArgumentException(
                    "Razorpay event ID is required."
            );
        }
    }

    private String limitFailureReason(
            String failureReason) {

        if (failureReason == null
                || failureReason.isBlank()) {

            return "Webhook processing failed.";
        }

        if (failureReason.length()
                <= MAXIMUM_FAILURE_REASON_LENGTH) {

            return failureReason;
        }

        return failureReason.substring(
                0,
                MAXIMUM_FAILURE_REASON_LENGTH
        );
    }
}