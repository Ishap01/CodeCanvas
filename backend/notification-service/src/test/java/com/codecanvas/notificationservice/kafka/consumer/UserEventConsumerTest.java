package com.codecanvas.notificationservice.kafka.consumer;

import com.codecanvas.notificationservice.dto.request.CreateNotificationRequest;
import com.codecanvas.notificationservice.enums.NotificationType;
import com.codecanvas.notificationservice.kafka.event.UserDeletedEvent;
import com.codecanvas.notificationservice.kafka.event.UserRegisteredEvent;
import com.codecanvas.notificationservice.kafka.event.UserUpdatedEvent;
import com.codecanvas.notificationservice.service.EmailService;
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
class UserEventConsumerTest {

    @Mock
    private NotificationService notificationService;

    @Mock
    private EmailService emailService;

    @Mock
    private Acknowledgment acknowledgment;

    @InjectMocks
    private UserEventConsumer userEventConsumer;

    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
    }

    @Test
    void testConsumeUserRegisteredEvent_Success() {
        UserRegisteredEvent event = UserRegisteredEvent.builder()
                .userId(userId)
                .username("johndoe")
                .fullName("John Doe")
                .email("john@example.com")
                .build();

        userEventConsumer.consumeUserRegisteredEvent(event, acknowledgment);

        ArgumentCaptor<CreateNotificationRequest> captor = ArgumentCaptor.forClass(CreateNotificationRequest.class);
        verify(notificationService, times(1)).createNotification(captor.capture());

        CreateNotificationRequest capturedRequest = captor.getValue();
        assertEquals(userId, capturedRequest.getUserId());
        assertEquals(NotificationType.USER_REGISTERED, capturedRequest.getNotificationType());
        assertEquals("Welcome to CodeCanvas!", capturedRequest.getTitle());
        assertTrue(capturedRequest.getMessage().contains("John Doe"));

        verify(emailService, times(1)).sendWelcomeEmail("john@example.com", "John Doe");
        verify(acknowledgment, times(1)).acknowledge();
    }

    @Test
    void testConsumeUserUpdatedEvent_Success() {
        UserUpdatedEvent event = UserUpdatedEvent.builder()
                .userId(userId)
                .username("johndoe")
                .fullName("John Doe")
                .email("john@example.com")
                .build();

        userEventConsumer.consumeUserUpdatedEvent(event, acknowledgment);

        ArgumentCaptor<CreateNotificationRequest> captor = ArgumentCaptor.forClass(CreateNotificationRequest.class);
        verify(notificationService, times(1)).createNotification(captor.capture());

        CreateNotificationRequest capturedRequest = captor.getValue();
        assertEquals(userId, capturedRequest.getUserId());
        assertEquals(NotificationType.USER_UPDATED, capturedRequest.getNotificationType());
        assertEquals("Profile Updated", capturedRequest.getTitle());

        verify(emailService, times(1)).sendProfileUpdatedEmail("john@example.com", "John Doe");
        verify(acknowledgment, times(1)).acknowledge();
    }

    @Test
    void testConsumeUserDeletedEvent_Success() {
        UserDeletedEvent event = UserDeletedEvent.builder()
                .userId(userId)
                .build();

        userEventConsumer.consumeUserDeletedEvent(event, acknowledgment);

        verify(acknowledgment, times(1)).acknowledge();
    }
}
