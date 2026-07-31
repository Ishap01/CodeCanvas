package com.codecanvas.notificationservice.dto.response;

import com.codecanvas.notificationservice.enums.NotificationType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class NotificationResponse {

    private UUID notificationId;

    private NotificationType notificationType;

    private String title;

    private String message;

    private Boolean isRead;

    private LocalDateTime createdAt;

}