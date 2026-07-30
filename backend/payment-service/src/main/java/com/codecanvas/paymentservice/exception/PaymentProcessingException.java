package com.codecanvas.paymentservice.exception;

public class PaymentProcessingException extends RuntimeException {

    public PaymentProcessingException() {
        super();
    }

    public PaymentProcessingException(
            String message) {

        super(message);
    }

    public PaymentProcessingException(
            String message,
            Throwable cause) {

        super(
                message,
                cause
        );
    }
}