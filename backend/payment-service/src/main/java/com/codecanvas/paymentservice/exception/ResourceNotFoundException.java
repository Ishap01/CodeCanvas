package com.codecanvas.paymentservice.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException() {
        super();
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(
            String resourceName,
            String fieldName,
            Object fieldValue) {

        super(resourceName
                + " not found with "
                + fieldName
                + " : "
                + fieldValue);
    }
}