package com.codecanvas.notificationservice.service.impl;

import com.codecanvas.notificationservice.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import jakarta.mail.internet.MimeMessage;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username:noreply@codecanvas.com}")
    private String fromEmail;

    @Override
    public void sendWelcomeEmail(String toEmail, String fullName) {
        String subject = "Welcome to CodeCanvas!";
        String text = String.format(
                "Hello %s,\n\nWelcome to CodeCanvas! We are excited to have you join our platform for building and sharing code snippets.\n\nBest regards,\nThe CodeCanvas Team",
                fullName != null ? fullName : "Developer"
        );
        sendNotificationEmail(toEmail, subject, text);
    }

    @Override
    public void sendProfileUpdatedEmail(String toEmail, String fullName) {
        String subject = "Your CodeCanvas Profile was Updated";
        String text = String.format(
                "Hello %s,\n\nYour profile details on CodeCanvas have been successfully updated.\n\nBest regards,\nThe CodeCanvas Team",
                fullName != null ? fullName : "Developer"
        );
        sendNotificationEmail(toEmail, subject, text);
    }

    @Override
    public void sendNotificationEmail(String toEmail, String subject, String body) {
        if (toEmail == null || toEmail.isBlank()) {
            log.warn("Email address is blank, skipping email dispatch.");
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject(subject);
            message.setText(body);

            javaMailSender.send(message);
            log.info("Successfully sent email to {} with subject: {}", toEmail, subject);
        } catch (Exception exception) {
            log.error("Failed to send email to {}: {}", toEmail, exception.getMessage());
        }
    }
}