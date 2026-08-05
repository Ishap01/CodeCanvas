package com.codecanvas.notificationservice.kafka.consumer;

import com.codecanvas.notificationservice.dto.request.CreateNotificationRequest;
import com.codecanvas.notificationservice.enums.NotificationType;
import com.codecanvas.notificationservice.kafka.event.SnippetCreatedEvent;
import com.codecanvas.notificationservice.kafka.event.SnippetDeletedEvent;
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
}
