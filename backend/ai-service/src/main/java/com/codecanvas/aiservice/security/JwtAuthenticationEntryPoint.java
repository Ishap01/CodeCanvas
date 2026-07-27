package com.codecanvas.aiservice.security;

import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationEntryPoint
        implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    public JwtAuthenticationEntryPoint(
            ObjectMapper objectMapper) {

        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException)
            throws IOException, ServletException {

        response.setStatus(
                HttpServletResponse.SC_UNAUTHORIZED
        );

        response.setContentType(
                MediaType.APPLICATION_JSON_VALUE
        );

        UnauthorizedResponse errorResponse =
                new UnauthorizedResponse();

        errorResponse.setSuccess(false);
        errorResponse.setStatus(401);
        errorResponse.setError("Unauthorized");
        errorResponse.setMessage(
                "Authentication token is missing or invalid"
        );
        errorResponse.setPath(
                request.getRequestURI()
        );
        errorResponse.setTimestamp(
                LocalDateTime.now()
        );

        objectMapper.writeValue(
                response.getOutputStream(),
                errorResponse
        );
    }

    private static class UnauthorizedResponse {

        private boolean success;
        private int status;
        private String error;
        private String message;
        private String path;
        private LocalDateTime timestamp;

        public UnauthorizedResponse() {
        }

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(
                LocalDateTime timestamp) {

            this.timestamp = timestamp;
        }
    }
}
