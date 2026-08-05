package com.codecanvas.notificationservice.dto.request;

import com.codecanvas.notificationservice.enums.NotificationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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