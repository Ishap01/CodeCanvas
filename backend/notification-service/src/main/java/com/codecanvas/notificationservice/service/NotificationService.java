package com.codecanvas.notificationservice.service;

import com.codecanvas.notificationservice.dto.request.CreateNotificationRequest;
import com.codecanvas.notificationservice.dto.response.ApiResponse;
import com.codecanvas.notificationservice.dto.response.NotificationResponse;

import java.util.List;
import java.util.UUID;

public interface NotificationService {

    ApiResponse createNotification(CreateNotificationRequest request);

    List<NotificationResponse> getNotifications(UUID userId);

    ApiResponse markAsRead(UUID notificationId);

    ApiResponse deleteNotification(UUID notificationId);

    long getUnreadCount(UUID userId);

}