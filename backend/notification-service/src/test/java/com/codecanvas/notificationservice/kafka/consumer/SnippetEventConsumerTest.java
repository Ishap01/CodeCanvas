package com.codecanvas.notificationservice.kafka.consumer;

import com.codecanvas.notificationservice.dto.request.CreateNotificationRequest;
import com.codecanvas.notificationservice.enums.NotificationType;
import com.codecanvas.notificationservice.kafka.event.CommentCreatedEvent;
import com.codecanvas.notificationservice.kafka.event.ReplyCreatedEvent;
import com.codecanvas.notificationservice.kafka.event.SnippetBookmarkedEvent;
import com.codecanvas.notificationservice.kafka.event.SnippetCreatedEvent;
import com.codecanvas.notificationservice.kafka.event.SnippetDeletedEvent;
import com.codecanvas.notificationservice.kafka.event.SnippetForkedEvent;
import com.codecanvas.notificationservice.kafka.event.SnippetLikedEvent;
import com.codecanvas.notificationservice.kafka.event.SnippetUpdatedEvent;
import com.codecanvas.notificationservice.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SnippetEventConsumerTest {

    @Mock
    private NotificationService notificationService;

    @Mock
    private Acknowledgment acknowledgment;

    @InjectMocks
    private SnippetEventConsumer snippetEventConsumer;

    private UUID snippetId;
    private UUID userId;

    @BeforeEach
    void setUp() {
        snippetId = UUID.randomUUID();
        userId = UUID.randomUUID();
    }

    @Test
    void testConsumeSnippetCreatedEvent_Success() {
        SnippetCreatedEvent event = SnippetCreatedEvent.builder()
                .snippetId(snippetId)
                .userId(userId)
                .title("Binary Search in Java")
                .build();

        snippetEventConsumer.consumeSnippetCreatedEvent(event, acknowledgment);

        ArgumentCaptor<CreateNotificationRequest> captor = ArgumentCaptor.forClass(CreateNotificationRequest.class);
        verify(notificationService, times(1)).createNotification(captor.capture());

        CreateNotificationRequest capturedRequest = captor.getValue();
        assertEquals(userId, capturedRequest.getUserId());
        assertEquals(NotificationType.SNIPPET_CREATED, capturedRequest.getNotificationType());
        assertEquals("Snippet Created", capturedRequest.getTitle());
        assertTrue(capturedRequest.getMessage().contains("Binary Search in Java"));

        verify(acknowledgment, times(1)).acknowledge();
    }

    @Test
    void testConsumeSnippetUpdatedEvent_Success() {
        SnippetUpdatedEvent event = SnippetUpdatedEvent.builder()
                .snippetId(snippetId)
                .userId(userId)
                .title("Binary Search in Java - Updated")
                .build();

        snippetEventConsumer.consumeSnippetUpdatedEvent(event, acknowledgment);

        ArgumentCaptor<CreateNotificationRequest> captor = ArgumentCaptor.forClass(CreateNotificationRequest.class);
        verify(notificationService, times(1)).createNotification(captor.capture());

        CreateNotificationRequest capturedRequest = captor.getValue();
        assertEquals(userId, capturedRequest.getUserId());
        assertEquals(NotificationType.SNIPPET_UPDATED, capturedRequest.getNotificationType());
        assertEquals("Snippet Updated", capturedRequest.getTitle());
        assertTrue(capturedRequest.getMessage().contains("Binary Search in Java - Updated"));

        verify(acknowledgment, times(1)).acknowledge();
    }

    @Test
    void testConsumeSnippetDeletedEvent_Success() {
        SnippetDeletedEvent event = SnippetDeletedEvent.builder()
                .snippetId(snippetId)
                .build();

        snippetEventConsumer.consumeSnippetDeletedEvent(event, acknowledgment);

        verify(acknowledgment, times(1)).acknowledge();
    }

    @Test
    void testConsumeSnippetLikedEvent_Success() {
        UUID ownerId = UUID.randomUUID();
        SnippetLikedEvent event = SnippetLikedEvent.builder()
                .snippetId(snippetId)
                .userId(userId)
                .snippetOwnerId(ownerId)
                .likeCount(1L)
                .build();

        snippetEventConsumer.consumeSnippetLikedEvent(event, acknowledgment);

        ArgumentCaptor<CreateNotificationRequest> captor = ArgumentCaptor.forClass(CreateNotificationRequest.class);
        verify(notificationService, times(1)).createNotification(captor.capture());

        CreateNotificationRequest capturedRequest = captor.getValue();
        assertEquals(ownerId, capturedRequest.getUserId());
        assertEquals(NotificationType.SNIPPET_LIKED, capturedRequest.getNotificationType());

        verify(acknowledgment, times(1)).acknowledge();
    }

    @Test
    void testConsumeSnippetBookmarkedEvent_Success() {
        SnippetBookmarkedEvent event = SnippetBookmarkedEvent.builder()
                .snippetId(snippetId)
                .userId(userId)
                .bookmarkCount(1L)
                .build();

        snippetEventConsumer.consumeSnippetBookmarkedEvent(event, acknowledgment);

        ArgumentCaptor<CreateNotificationRequest> captor = ArgumentCaptor.forClass(CreateNotificationRequest.class);
        verify(notificationService, times(1)).createNotification(captor.capture());

        CreateNotificationRequest capturedRequest = captor.getValue();
        assertEquals(userId, capturedRequest.getUserId());
        assertEquals(NotificationType.SNIPPET_BOOKMARKED, capturedRequest.getNotificationType());

        verify(acknowledgment, times(1)).acknowledge();
    }

    @Test
    void testConsumeCommentCreatedEvent_Success() {
        UUID ownerId = UUID.randomUUID();
        UUID commentId = UUID.randomUUID();
        CommentCreatedEvent event = CommentCreatedEvent.builder()
                .commentId(commentId)
                .snippetId(snippetId)
                .snippetOwnerId(ownerId)
                .userId(userId)
                .content("Great snippet!")
                .build();

        snippetEventConsumer.consumeCommentCreatedEvent(event, acknowledgment);

        ArgumentCaptor<CreateNotificationRequest> captor = ArgumentCaptor.forClass(CreateNotificationRequest.class);
        verify(notificationService, times(1)).createNotification(captor.capture());

        CreateNotificationRequest capturedRequest = captor.getValue();
        assertEquals(ownerId, capturedRequest.getUserId());
        assertEquals(NotificationType.COMMENT_CREATED, capturedRequest.getNotificationType());

        verify(acknowledgment, times(1)).acknowledge();
    }

    @Test
    void testConsumeReplyCreatedEvent_Success() {
        UUID ownerId = UUID.randomUUID();
        UUID replyId = UUID.randomUUID();
        ReplyCreatedEvent event = ReplyCreatedEvent.builder()
                .replyId(replyId)
                .parentCommentId(UUID.randomUUID())
                .commentOwnerId(ownerId)
                .userId(userId)
                .content("Thanks!")
                .build();

        snippetEventConsumer.consumeReplyCreatedEvent(event, acknowledgment);

        ArgumentCaptor<CreateNotificationRequest> captor = ArgumentCaptor.forClass(CreateNotificationRequest.class);
        verify(notificationService, times(1)).createNotification(captor.capture());

        CreateNotificationRequest capturedRequest = captor.getValue();
        assertEquals(ownerId, capturedRequest.getUserId());
        assertEquals(NotificationType.REPLY_CREATED, capturedRequest.getNotificationType());

        verify(acknowledgment, times(1)).acknowledge();
    }

    @Test
    void testConsumeSnippetForkedEvent_Success() {
        UUID ownerId = UUID.randomUUID();
        SnippetForkedEvent event = SnippetForkedEvent.builder()
                .originalSnippetId(snippetId)
                .forkedSnippetId(UUID.randomUUID())
                .originalOwnerId(ownerId)
                .forkedBy(userId)
                .forkCount(1L)
                .build();

        snippetEventConsumer.consumeSnippetForkedEvent(event, acknowledgment);

        ArgumentCaptor<CreateNotificationRequest> captor = ArgumentCaptor.forClass(CreateNotificationRequest.class);
        verify(notificationService, times(1)).createNotification(captor.capture());

        CreateNotificationRequest capturedRequest = captor.getValue();
        assertEquals(ownerId, capturedRequest.getUserId());
        assertEquals(NotificationType.SNIPPET_FORKED, capturedRequest.getNotificationType());

        verify(acknowledgment, times(1)).acknowledge();
    }
}
