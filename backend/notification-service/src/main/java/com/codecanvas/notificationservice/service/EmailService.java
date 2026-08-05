package com.codecanvas.notificationservice.service;

public interface EmailService {

    void sendWelcomeEmail(String toEmail, String fullName);

    void sendProfileUpdatedEmail(String toEmail, String fullName);

    void sendNotificationEmail(String toEmail, String subject, String body);
}
