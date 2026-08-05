package com.codecanvas.notificationservice.kafka.consumer;

import com.codecanvas.notificationservice.dto.request.CreateNotificationRequest;
import com.codecanvas.notificationservice.enums.NotificationType;
import com.codecanvas.notificationservice.kafka.constant.KafkaTopics;
import com.codecanvas.notificationservice.kafka.event.CommentCreatedEvent;
import com.codecanvas.notificationservice.kafka.event.ReplyCreatedEvent;
import com.codecanvas.notificationservice.kafka.event.SnippetBookmarkedEvent;
import com.codecanvas.notificationservice.kafka.event.SnippetCreatedEvent;
import com.codecanvas.notificationservice.kafka.event.SnippetDeletedEvent;
import com.codecanvas.notificationservice.kafka.event.SnippetForkedEvent;
import com.codecanvas.notificationservice.kafka.event.SnippetLikedEvent;
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

    @KafkaListener(topics = KafkaTopics.SNIPPET_CREATED, containerFactory = "snippetCreatedKafkaListenerContainerFactory")
    public void consumeSnippetCreatedEvent(SnippetCreatedEvent event, Acknowledgment acknowledgment) {
        log.info("Received Snippet Created Event for SnippetId: {}, UserId: {}", event.getSnippetId(),
                event.getUserId());

        try {
            if (event.getUserId() != null) {
                CreateNotificationRequest request = CreateNotificationRequest.builder()
                        .userId(event.getUserId())
                        .notificationType(NotificationType.SNIPPET_CREATED)
                        .title("Snippet Created")
                        .message(String.format("Your snippet '%s' has been created successfully.",
                                event.getTitle() != null ? event.getTitle() : "Untitled"))
                        .build();

                notificationService.createNotification(request);
            }

            log.info("Successfully processed Snippet Created Event for SnippetId: {}", event.getSnippetId());
        } catch (Exception e) {
            log.error("Error processing Snippet Created Event for SnippetId: {}: {}", event.getSnippetId(),
                    e.getMessage(), e);
        } finally {
            if (acknowledgment != null) {
                acknowledgment.acknowledge();
            }
        }
    }

    @KafkaListener(topics = KafkaTopics.SNIPPET_UPDATED, containerFactory = "snippetUpdatedKafkaListenerContainerFactory")
    public void consumeSnippetUpdatedEvent(SnippetUpdatedEvent event, Acknowledgment acknowledgment) {
        log.info("Received Snippet Updated Event for SnippetId: {}, UserId: {}", event.getSnippetId(),
                event.getUserId());

        try {
            if (event.getUserId() != null) {
                CreateNotificationRequest request = CreateNotificationRequest.builder()
                        .userId(event.getUserId())
                        .notificationType(NotificationType.SNIPPET_UPDATED)
                        .title("Snippet Updated")
                        .message(String.format("Your snippet '%s' has been updated.",
                                event.getTitle() != null ? event.getTitle() : "Untitled"))
                        .build();

                notificationService.createNotification(request);
            }

            log.info("Successfully processed Snippet Updated Event for SnippetId: {}", event.getSnippetId());
        } catch (Exception e) {
            log.error("Error processing Snippet Updated Event for SnippetId: {}: {}", event.getSnippetId(),
                    e.getMessage(), e);
        } finally {
            if (acknowledgment != null) {
                acknowledgment.acknowledge();
            }
        }
    }

    @KafkaListener(topics = KafkaTopics.SNIPPET_DELETED, containerFactory = "snippetDeletedKafkaListenerContainerFactory")
    public void consumeSnippetDeletedEvent(SnippetDeletedEvent event, Acknowledgment acknowledgment) {
        log.info("Received Snippet Deleted Event for SnippetId: {}", event.getSnippetId());

        try {
            log.info("Successfully processed Snippet Deleted Event for SnippetId: {}", event.getSnippetId());
        } catch (Exception e) {
            log.error("Error processing Snippet Deleted Event for SnippetId: {}: {}", event.getSnippetId(),
                    e.getMessage(), e);
        } finally {
            if (acknowledgment != null) {
                acknowledgment.acknowledge();
            }
        }
    }

    @KafkaListener(topics = KafkaTopics.SNIPPET_LIKED, containerFactory = "snippetLikedKafkaListenerContainerFactory")
    public void consumeSnippetLikedEvent(SnippetLikedEvent event, Acknowledgment acknowledgment) {
        log.info("Received Snippet Liked Event for SnippetId: {}, UserId: {}, OwnerId: {}",
                event.getSnippetId(), event.getUserId(), event.getSnippetOwnerId());

        try {
            if (event.getSnippetOwnerId() != null && !event.getSnippetOwnerId().equals(event.getUserId())) {
                CreateNotificationRequest request = CreateNotificationRequest.builder()
                        .userId(event.getSnippetOwnerId())
                        .notificationType(NotificationType.SNIPPET_LIKED)
                        .title("Snippet Liked")
                        .message("Your snippet has been liked.")
                        .build();

                notificationService.createNotification(request);
            }

            log.info("Successfully processed Snippet Liked Event for SnippetId: {}", event.getSnippetId());
        } catch (Exception e) {
            log.error("Error processing Snippet Liked Event for SnippetId: {}: {}", event.getSnippetId(),
                    e.getMessage(), e);
        } finally {
            if (acknowledgment != null) {
                acknowledgment.acknowledge();
            }
        }
    }

    @KafkaListener(topics = KafkaTopics.SNIPPET_BOOKMARKED, containerFactory = "snippetBookmarkedKafkaListenerContainerFactory")
    public void consumeSnippetBookmarkedEvent(SnippetBookmarkedEvent event, Acknowledgment acknowledgment) {
        log.info("Received Snippet Bookmarked Event for SnippetId: {}, UserId: {}",
                event.getSnippetId(), event.getUserId());

        try {
            if (event.getUserId() != null) {
                CreateNotificationRequest request = CreateNotificationRequest.builder()
                        .userId(event.getUserId())
                        .notificationType(NotificationType.SNIPPET_BOOKMARKED)
                        .title("Snippet Bookmarked")
                        .message("You bookmarked a snippet.")
                        .build();

                notificationService.createNotification(request);
            }

            log.info("Successfully processed Snippet Bookmarked Event for SnippetId: {}", event.getSnippetId());
        } catch (Exception e) {
            log.error("Error processing Snippet Bookmarked Event for SnippetId: {}: {}", event.getSnippetId(),
                    e.getMessage(), e);
        } finally {
            if (acknowledgment != null) {
                acknowledgment.acknowledge();
            }
        }
    }

    @KafkaListener(topics = KafkaTopics.COMMENT_CREATED, containerFactory = "commentCreatedKafkaListenerContainerFactory")
    public void consumeCommentCreatedEvent(CommentCreatedEvent event, Acknowledgment acknowledgment) {
        log.info("Received Comment Created Event for CommentId: {}, SnippetId: {}, UserId: {}, OwnerId: {}",
                event.getCommentId(), event.getSnippetId(), event.getUserId(), event.getSnippetOwnerId());

        try {
            if (event.getSnippetOwnerId() != null && !event.getSnippetOwnerId().equals(event.getUserId())) {
                CreateNotificationRequest request = CreateNotificationRequest.builder()
                        .userId(event.getSnippetOwnerId())
                        .notificationType(NotificationType.COMMENT_CREATED)
                        .title("New Comment")
                        .message(String.format("New comment on your snippet: '%s'",
                                event.getContent() != null ? event.getContent() : ""))
                        .build();

                notificationService.createNotification(request);
            }

            log.info("Successfully processed Comment Created Event for CommentId: {}", event.getCommentId());
        } catch (Exception e) {
            log.error("Error processing Comment Created Event for CommentId: {}: {}", event.getCommentId(),
                    e.getMessage(), e);
        } finally {
            if (acknowledgment != null) {
                acknowledgment.acknowledge();
            }
        }
    }

    @KafkaListener(topics = KafkaTopics.REPLY_CREATED, containerFactory = "replyCreatedKafkaListenerContainerFactory")
    public void consumeReplyCreatedEvent(ReplyCreatedEvent event, Acknowledgment acknowledgment) {
        log.info("Received Reply Created Event for ReplyId: {}, ParentCommentId: {}, UserId: {}, CommentOwnerId: {}",
                event.getReplyId(), event.getParentCommentId(), event.getUserId(), event.getCommentOwnerId());

        try {
            if (event.getCommentOwnerId() != null && !event.getCommentOwnerId().equals(event.getUserId())) {
                CreateNotificationRequest request = CreateNotificationRequest.builder()
                        .userId(event.getCommentOwnerId())
                        .notificationType(NotificationType.REPLY_CREATED)
                        .title("New Reply")
                        .message(String.format("New reply to your comment: '%s'",
                                event.getContent() != null ? event.getContent() : ""))
                        .build();

                notificationService.createNotification(request);
            }

            log.info("Successfully processed Reply Created Event for ReplyId: {}", event.getReplyId());
        } catch (Exception e) {
            log.error("Error processing Reply Created Event for ReplyId: {}: {}", event.getReplyId(),
                    e.getMessage(), e);
        } finally {
            if (acknowledgment != null) {
                acknowledgment.acknowledge();
            }
        }
    }

    @KafkaListener(topics = KafkaTopics.SNIPPET_FORKED, containerFactory = "snippetForkedKafkaListenerContainerFactory")
    public void consumeSnippetForkedEvent(SnippetForkedEvent event, Acknowledgment acknowledgment) {
        log.info("Received Snippet Forked Event for OriginalSnippetId: {}, ForkedSnippetId: {}, OriginalOwnerId: {}, ForkedBy: {}",
                event.getOriginalSnippetId(), event.getForkedSnippetId(), event.getOriginalOwnerId(), event.getForkedBy());

        try {
            if (event.getOriginalOwnerId() != null && !event.getOriginalOwnerId().equals(event.getForkedBy())) {
                CreateNotificationRequest request = CreateNotificationRequest.builder()
                        .userId(event.getOriginalOwnerId())
                        .notificationType(NotificationType.SNIPPET_FORKED)
                        .title("Snippet Forked")
                        .message("Your snippet has been forked.")
                        .build();

                notificationService.createNotification(request);
            }

            log.info("Successfully processed Snippet Forked Event for OriginalSnippetId: {}", event.getOriginalSnippetId());
        } catch (Exception e) {
            log.error("Error processing Snippet Forked Event for OriginalSnippetId: {}: {}", event.getOriginalSnippetId(),
                    e.getMessage(), e);
        } finally {
            if (acknowledgment != null) {
                acknowledgment.acknowledge();
            }
        }
    }
}
