package com.codecanvas.notificationservice.kafka.consumer;

import com.codecanvas.notificationservice.dto.request.CreateNotificationRequest;
import com.codecanvas.notificationservice.enums.NotificationType;
import com.codecanvas.notificationservice.kafka.constant.KafkaTopics;
import com.codecanvas.notificationservice.kafka.event.SnippetCreatedEvent;
import com.codecanvas.notificationservice.kafka.event.SnippetDeletedEvent;
import com.codecanvas.notificationservice.kafka.event.SnippetUpdatedEvent;
import com.codecanvas.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SnippetEventConsumer {

    private final NotificationService notificationService;

    @KafkaListener(
            topics = KafkaTopics.SNIPPET_CREATED,
            containerFactory = "snippetCreatedKafkaListenerContainerFactory"
    )
    public void consumeSnippetCreatedEvent(SnippetCreatedEvent event, Acknowledgment acknowledgment) {
        log.info("Received Snippet Created Event for SnippetId: {}, UserId: {}", event.getSnippetId(), event.getUserId());

        try {
            if (event.getUserId() != null) {
                CreateNotificationRequest request = CreateNotificationRequest.builder()
                        .userId(event.getUserId())
                        .notificationType(NotificationType.SNIPPET_CREATED)
                        .title("Snippet Created")
                        .message(String.format("Your snippet '%s' has been created successfully.", event.getTitle() != null ? event.getTitle() : "Untitled"))
                        .build();

                notificationService.createNotification(request);
            }

            log.info("Successfully processed Snippet Created Event for SnippetId: {}", event.getSnippetId());
        } catch (Exception e) {
            log.error("Error processing Snippet Created Event for SnippetId: {}: {}", event.getSnippetId(), e.getMessage(), e);
        } finally {
            if (acknowledgment != null) {
                acknowledgment.acknowledge();
            }
        }
    }

    @KafkaListener(
            topics = KafkaTopics.SNIPPET_UPDATED,
            containerFactory = "snippetUpdatedKafkaListenerContainerFactory"
    )
    public void consumeSnippetUpdatedEvent(SnippetUpdatedEvent event, Acknowledgment acknowledgment) {
        log.info("Received Snippet Updated Event for SnippetId: {}, UserId: {}", event.getSnippetId(), event.getUserId());

        try {
            if (event.getUserId() != null) {
                CreateNotificationRequest request = CreateNotificationRequest.builder()
                        .userId(event.getUserId())
                        .notificationType(NotificationType.SNIPPET_UPDATED)
                        .title("Snippet Updated")
                        .message(String.format("Your snippet '%s' has been updated.", event.getTitle() != null ? event.getTitle() : "Untitled"))
                        .build();

                notificationService.createNotification(request);
            }

            log.info("Successfully processed Snippet Updated Event for SnippetId: {}", event.getSnippetId());
        } catch (Exception e) {
            log.error("Error processing Snippet Updated Event for SnippetId: {}: {}", event.getSnippetId(), e.getMessage(), e);
        } finally {
            if (acknowledgment != null) {
                acknowledgment.acknowledge();
            }
        }
    }

    @KafkaListener(
            topics = KafkaTopics.SNIPPET_DELETED,
            containerFactory = "snippetDeletedKafkaListenerContainerFactory"
    )
    public void consumeSnippetDeletedEvent(SnippetDeletedEvent event, Acknowledgment acknowledgment) {
        log.info("Received Snippet Deleted Event for SnippetId: {}", event.getSnippetId());

        try {
            log.info("Successfully processed Snippet Deleted Event for SnippetId: {}", event.getSnippetId());
        } catch (Exception e) {
            log.error("Error processing Snippet Deleted Event for SnippetId: {}: {}", event.getSnippetId(), e.getMessage(), e);
        } finally {
            if (acknowledgment != null) {
                acknowledgment.acknowledge();
            }
        }
    }
}
