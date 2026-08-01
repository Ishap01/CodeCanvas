package com.codecanvas.paymentservice.exception;

public class UserServiceIntegrationException extends RuntimeException {

    public UserServiceIntegrationException(String message) {
        super(message);
    }

    public UserServiceIntegrationException(String message, Throwable cause) {
        super(message, cause);
    }
}