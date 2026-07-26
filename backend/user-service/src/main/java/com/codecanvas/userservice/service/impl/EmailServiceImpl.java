package com.codecanvas.userservice.service.impl;

import com.codecanvas.userservice.service.EmailService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendOtpEmail(String to, String otp) {

        SimpleMailMessage message =
                new SimpleMailMessage();

        message.setTo(to);

        message.setSubject(
                "CodeCanvas Password Reset OTP"
        );

        message.setText(
                "Hello,\n\n"
                        + "Your OTP for password reset is : "
                        + otp
                        + "\n\n"
                        + "This OTP is valid for 10 minutes."
                        + "\n\n"
                        + "If you didn't request this, please ignore this email."
                        + "\n\n"
                        + "Regards,\n"
                        + "CodeCanvas Team"
        );

        mailSender.send(message);
    }
}