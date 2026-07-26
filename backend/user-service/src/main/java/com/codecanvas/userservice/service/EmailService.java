package com.codecanvas.userservice.service;

public interface EmailService {

    void sendOtpEmail(
            String to,
            String otp
    );

}