package com.codecanvas.paymentservice.exception;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(
                    GlobalExceptionHandler.class
            );

    @ExceptionHandler({
            PaymentNotFoundException.class,
            ResourceNotFoundException.class,
            EntityNotFoundException.class
    })
    public ResponseEntity<ErrorResponse> handleNotFound(
            RuntimeException exception,
            HttpServletRequest request) {

        return buildResponse(
                HttpStatus.NOT_FOUND,
                exception.getMessage(),
                request,
                null
        );
    }

    @ExceptionHandler({
            UnauthorizedActionException.class,
            SecurityException.class
    })
    public ResponseEntity<ErrorResponse> handleUnauthorized(
            RuntimeException exception,
            HttpServletRequest request) {

        return buildResponse(
                HttpStatus.UNAUTHORIZED,
                exception.getMessage(),
                request,
                null
        );
    }

    @ExceptionHandler({
            RefundNotAllowedException.class,
            PaymentVerificationException.class
    })
    public ResponseEntity<ErrorResponse> handleUnprocessableEntity(
            RuntimeException exception,
            HttpServletRequest request) {

        return buildResponse(
                HttpStatus.UNPROCESSABLE_ENTITY,
                exception.getMessage(),
                request,
                null
        );
    }

    @ExceptionHandler({
            PaymentProcessingException.class,
            RazorpayIntegrationException.class,
            UserServiceIntegrationException.class
    })
    public ResponseEntity<ErrorResponse> handlePaymentProcessing(
            RuntimeException exception,
            HttpServletRequest request) {

        LOGGER.error(
                "Payment processing error: {}",
                exception.getMessage(),
                exception
        );

        return buildResponse(
                HttpStatus.BAD_GATEWAY,
                exception.getMessage(),
                request,
                null
        );
    }

    @ExceptionHandler(
            MethodArgumentNotValidException.class
    )
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException exception,
            HttpServletRequest request) {

        Map<String, String> validationErrors =
                new LinkedHashMap<>();

        for (FieldError fieldError :
                exception.getBindingResult()
                        .getFieldErrors()) {

            validationErrors.putIfAbsent(
                    fieldError.getField(),
                    fieldError.getDefaultMessage()
            );
        }

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "Request validation failed",
                request,
                validationErrors
        );
    }

    @ExceptionHandler(
            HttpMessageNotReadableException.class
    )
    public ResponseEntity<ErrorResponse>
    handleUnreadableRequest(
            HttpMessageNotReadableException exception,
            HttpServletRequest request) {

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "Request body is missing or invalid",
                request,
                null
        );
    }

    @ExceptionHandler(
            MissingRequestHeaderException.class
    )
    public ResponseEntity<ErrorResponse>
    handleMissingHeader(
            MissingRequestHeaderException exception,
            HttpServletRequest request) {

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "Required request header is missing: "
                        + exception.getHeaderName(),
                request,
                null
        );
    }

    @ExceptionHandler(
            IllegalArgumentException.class
    )
    public ResponseEntity<ErrorResponse>
    handleIllegalArgument(
            IllegalArgumentException exception,
            HttpServletRequest request) {

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                exception.getMessage(),
                request,
                null
        );
    }

    @ExceptionHandler(
            IllegalStateException.class
    )
    public ResponseEntity<ErrorResponse>
    handleIllegalState(
            IllegalStateException exception,
            HttpServletRequest request) {

        return buildResponse(
                HttpStatus.CONFLICT,
                exception.getMessage(),
                request,
                null
        );
    }

    @ExceptionHandler(
            DataIntegrityViolationException.class
    )
    public ResponseEntity<ErrorResponse>
    handleDataIntegrityViolation(
            DataIntegrityViolationException exception,
            HttpServletRequest request) {

        LOGGER.error(
                "Database constraint violation",
                exception
        );

        return buildResponse(
                HttpStatus.CONFLICT,
                "Database constraint violation occurred",
                request,
                null
        );
    }

    @ExceptionHandler(
            FeignException.class
    )
    public ResponseEntity<ErrorResponse>
    handleFeignException(
            FeignException exception,
            HttpServletRequest request) {

        LOGGER.error(
                "Communication with another service failed",
                exception
        );

        return buildResponse(
                HttpStatus.BAD_GATEWAY,
                "Communication with dependent service failed",
                request,
                null
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse>
    handleUnexpectedException(
            Exception exception,
            HttpServletRequest request) {

        LOGGER.error(
                "Unexpected payment-service error",
                exception
        );

        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected internal server error occurred",
                request,
                null
        );
    }

    private ResponseEntity<ErrorResponse> buildResponse(
            HttpStatus httpStatus,
            String message,
            HttpServletRequest request,
            Map<String, String> validationErrors) {

        String safeMessage =
                message == null || message.isBlank()
                        ? httpStatus.getReasonPhrase()
                        : message;

        ErrorResponse response =
                new ErrorResponse(
                        LocalDateTime.now(),
                        httpStatus.value(),
                        httpStatus.getReasonPhrase(),
                        safeMessage,
                        request.getRequestURI(),
                        validationErrors
                );

        return ResponseEntity
                .status(httpStatus)
                .body(response);
    }
}