package com.codecanvas.notificationservice.controller;

import com.codecanvas.notificationservice.dto.request.CreateNotificationRequest;
import com.codecanvas.notificationservice.dto.response.ApiResponse;
import com.codecanvas.notificationservice.dto.response.NotificationResponse;
import com.codecanvas.notificationservice.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping
    public ApiResponse createNotification(
            @Valid @RequestBody CreateNotificationRequest request) {

        return notificationService.createNotification(request);
    }

    @GetMapping("/{userId}")
    public List<NotificationResponse> getNotifications(
            @PathVariable UUID userId) {

        return notificationService.getNotifications(userId);
    }

    @PutMapping("/{notificationId}/read")
    public ApiResponse markAsRead(
            @PathVariable UUID notificationId) {

        return notificationService.markAsRead(notificationId);
    }

    @DeleteMapping("/{notificationId}")
    public ApiResponse deleteNotification(
            @PathVariable UUID notificationId) {

        return notificationService.deleteNotification(notificationId);
    }

    @GetMapping("/{userId}/unread-count")
    public long getUnreadCount(
            @PathVariable UUID userId) {

        return notificationService.getUnreadCount(userId);
    }

}