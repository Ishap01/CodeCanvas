package com.codecanvas.notificationservice.service.impl;

import com.codecanvas.notificationservice.dto.request.CreateNotificationRequest;
import com.codecanvas.notificationservice.dto.response.ApiResponse;
import com.codecanvas.notificationservice.dto.response.NotificationResponse;
import com.codecanvas.notificationservice.entity.Notification;
import com.codecanvas.notificationservice.exception.NotificationNotFoundException;
import com.codecanvas.notificationservice.mapper.NotificationMapper;
import com.codecanvas.notificationservice.repository.NotificationRepository;
import com.codecanvas.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Override
    public ApiResponse createNotification(CreateNotificationRequest request) {

        Notification notification = Notification.builder()
                .userId(request.getUserId())
                .notificationType(request.getNotificationType())
                .title(request.getTitle())
                .message(request.getMessage())
                .build();

        notificationRepository.save(notification);

        return new ApiResponse("Notification created successfully.");
    }

    @Override
    public List<NotificationResponse> getNotifications(UUID userId) {

        return notificationRepository
                .findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(NotificationMapper::toResponse)
                .toList();
    }

    @Override
    public ApiResponse markAsRead(UUID notificationId) {

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() ->
                        new NotificationNotFoundException("Notification not found."));

        notification.setIsRead(true);

        notificationRepository.save(notification);

        return new ApiResponse("Notification marked as read.");
    }

    @Override
    public ApiResponse deleteNotification(UUID notificationId) {

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() ->
                        new NotificationNotFoundException("Notification not found."));

        notificationRepository.delete(notification);

        return new ApiResponse("Notification deleted successfully.");
    }

    @Override
    public long getUnreadCount(UUID userId) {

        return notificationRepository.countByUserIdAndIsReadFalse(userId);

    }

}