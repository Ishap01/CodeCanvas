package com.codecanvas.notificationservice.repository;

import com.codecanvas.notificationservice.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface NotificationRepository
        extends JpaRepository<Notification, UUID> {
}
