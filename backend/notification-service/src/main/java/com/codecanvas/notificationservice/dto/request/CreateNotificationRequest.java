package com.codecanvas.notificationservice.dto.request;

import com.codecanvas.notificationservice.enums.NotificationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateNotificationRequest {

    @NotNull(message = "User Id is required")
    private UUID userId;

    @NotNull(message = "Notification type is required")
    private NotificationType notificationType;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Message is required")
    private String message;

}