package com.codecanvas.userservice.service;
import com.codecanvas.userservice.dto.request.ForgotPasswordRequest;
import com.codecanvas.userservice.dto.request.VerifyOtpRequest;
import com.codecanvas.userservice.dto.request.ResetPasswordRequest;
import com.codecanvas.userservice.dto.request.ChangePasswordRequest;

import com.codecanvas.userservice.dto.request.LoginRequest;
import com.codecanvas.userservice.dto.request.RegisterRequest;
import com.codecanvas.userservice.dto.response.ApiResponse;
import com.codecanvas.userservice.dto.response.AuthResponse;

public interface AuthService {

    ApiResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    void forgotPassword(ForgotPasswordRequest request);

    String verifyOtp(VerifyOtpRequest request);

    String resetPassword(ResetPasswordRequest request);

    String changePassword(
            String email,
            ChangePasswordRequest request
    );
}