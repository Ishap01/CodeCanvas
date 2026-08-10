package com.codecanvas.notificationservice.kafka.consumer;

import com.codecanvas.notificationservice.dto.request.CreateNotificationRequest;
import com.codecanvas.notificationservice.enums.NotificationType;
import com.codecanvas.notificationservice.kafka.constant.KafkaTopics;
import com.codecanvas.notificationservice.kafka.event.UserDeletedEvent;
import com.codecanvas.notificationservice.kafka.event.UserRegisteredEvent;
import com.codecanvas.notificationservice.kafka.event.UserUpdatedEvent;
import com.codecanvas.notificationservice.service.EmailService;
import com.codecanvas.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventConsumer {

    private final NotificationService notificationService;
    private final EmailService emailService;

    @KafkaListener(
            topics = KafkaTopics.USER_REGISTERED,
            containerFactory = "userRegisteredKafkaListenerContainerFactory"
    )
    public void consumeUserRegisteredEvent(UserRegisteredEvent event, Acknowledgment acknowledgment) {
        log.info("Received User Registered Event for UserId: {}, Email: {}", event.getUserId(), event.getEmail());

        try {
            CreateNotificationRequest request = CreateNotificationRequest.builder()
                    .userId(event.getUserId())
                    .notificationType(NotificationType.USER_REGISTERED)
                    .title("Welcome to CodeCanvas!")
                    .message(String.format("Welcome %s! Your account has been registered successfully.", event.getFullName() != null ? event.getFullName() : event.getUsername()))
                    .build();

            notificationService.createNotification(request);

            if (event.getEmail() != null && !event.getEmail().isBlank()) {
                emailService.sendWelcomeEmail(event.getEmail(), event.getFullName());
            }

            log.info("Successfully processed User Registered Event for UserId: {}", event.getUserId());
        } catch (Exception e) {
            log.error("Error processing User Registered Event for UserId: {}: {}", event.getUserId(), e.getMessage(), e);
        } finally {
            if (acknowledgment != null) {
                acknowledgment.acknowledge();
            }
        }
    }

    @KafkaListener(
            topics = KafkaTopics.USER_UPDATED,
            containerFactory = "userUpdatedKafkaListenerContainerFactory"
    )
    public void consumeUserUpdatedEvent(UserUpdatedEvent event, Acknowledgment acknowledgment) {
        log.info("Received User Updated Event for UserId: {}", event.getUserId());

        try {
            CreateNotificationRequest request = CreateNotificationRequest.builder()
                    .userId(event.getUserId())
                    .notificationType(NotificationType.USER_UPDATED)
                    .title("Profile Updated")
                    .message("Your profile information has been successfully updated.")
                    .build();

            notificationService.createNotification(request);

            if (event.getEmail() != null && !event.getEmail().isBlank()) {
                emailService.sendProfileUpdatedEmail(event.getEmail(), event.getFullName());
            }

            log.info("Successfully processed User Updated Event for UserId: {}", event.getUserId());
        } catch (Exception e) {
            log.error("Error processing User Updated Event for UserId: {}: {}", event.getUserId(), e.getMessage(), e);
        } finally {
            if (acknowledgment != null) {
                acknowledgment.acknowledge();
            }
        }
    }

    @KafkaListener(
            topics = KafkaTopics.USER_DELETED,
            containerFactory = "userDeletedKafkaListenerContainerFactory"
    )
    public void consumeUserDeletedEvent(UserDeletedEvent event, Acknowledgment acknowledgment) {
        log.info("Received User Deleted Event for UserId: {}", event.getUserId());

        try {
            log.info("User deleted event processed for UserId: {}", event.getUserId());
        } catch (Exception e) {
            log.error("Error processing User Deleted Event for UserId: {}: {}", event.getUserId(), e.getMessage(), e);
        } finally {
            if (acknowledgment != null) {
                acknowledgment.acknowledge();
            }
        }
    }
}