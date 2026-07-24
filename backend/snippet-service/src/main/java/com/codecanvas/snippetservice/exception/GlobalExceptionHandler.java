package com.codecanvas.snippetservice.exception;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
            ResourceNotFoundException exception,
            HttpServletRequest request) {

        ErrorResponse response = createErrorResponse(
                HttpStatus.NOT_FOUND,
                exception.getMessage(),
                request.getRequestURI(),
                null
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }

    @ExceptionHandler(UnauthorizedActionException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedActionException(
            UnauthorizedActionException exception,
            HttpServletRequest request) {

        ErrorResponse response = createErrorResponse(
                HttpStatus.FORBIDDEN,
                exception.getMessage(),
                request.getRequestURI(),
                null
        );

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(response);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateResourceException(
            DuplicateResourceException exception,
            HttpServletRequest request) {

        ErrorResponse response = createErrorResponse(
                HttpStatus.CONFLICT,
                exception.getMessage(),
                request.getRequestURI(),
                null
        );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException exception,
            HttpServletRequest request) {

        Map<String, String> validationErrors = new LinkedHashMap<>();

        for (FieldError fieldError
                : exception.getBindingResult().getFieldErrors()) {

            validationErrors.put(
                    fieldError.getField(),
                    fieldError.getDefaultMessage()
            );
        }

        ErrorResponse response = createErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Request validation failed",
                request.getRequestURI(),
                validationErrors
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse>
            handleHttpMessageNotReadableException(
                    HttpMessageNotReadableException exception,
                    HttpServletRequest request) {

        System.err.println(
                "========== INVALID REQUEST BODY =========="
        );
        exception.printStackTrace();
        System.err.println(
                "=========================================="
        );

        ErrorResponse response = createErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Invalid request body or enum value",
                request.getRequestURI(),
                null
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ErrorResponse>
            handleMissingRequestHeaderException(
                    MissingRequestHeaderException exception,
                    HttpServletRequest request) {

        ErrorResponse response = createErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Required header is missing: "
                        + exception.getHeaderName(),
                request.getRequestURI(),
                null
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse>
            handleDataIntegrityViolationException(
                    DataIntegrityViolationException exception,
                    HttpServletRequest request) {

        System.err.println(
                "========== DATA INTEGRITY EXCEPTION =========="
        );
        exception.printStackTrace();
        System.err.println(
                "=============================================="
        );

        String message = "Database constraint violation";

        Throwable mostSpecificCause =
                exception.getMostSpecificCause();

        if (mostSpecificCause != null
                && mostSpecificCause.getMessage() != null
                && !mostSpecificCause.getMessage().isBlank()) {

            message = mostSpecificCause.getMessage();
        }

        ErrorResponse response = createErrorResponse(
                HttpStatus.CONFLICT,
                message,
                request.getRequestURI(),
                null
        );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse>
            handleIllegalArgumentException(
                    IllegalArgumentException exception,
                    HttpServletRequest request) {

        ErrorResponse response = createErrorResponse(
                HttpStatus.BAD_REQUEST,
                exception.getMessage(),
                request.getRequestURI(),
                null
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(
            Exception exception,
            HttpServletRequest request) {

        System.err.println(
                "========== ACTUAL EXCEPTION =========="
        );
        exception.printStackTrace();
        System.err.println(
                "======================================"
        );

        String message = exception.getClass().getSimpleName();

        if (exception.getMessage() != null
                && !exception.getMessage().isBlank()) {

            message = message + ": " + exception.getMessage();
        }

        ErrorResponse response = createErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                message,
                request.getRequestURI(),
                null
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }

    private ErrorResponse createErrorResponse(
            HttpStatus status,
            String message,
            String path,
            Map<String, String> validationErrors) {

        ErrorResponse response = new ErrorResponse();

        response.setSuccess(false);
        response.setStatus(status.value());
        response.setError(status.getReasonPhrase());
        response.setMessage(message);
        response.setPath(path);
        response.setTimestamp(LocalDateTime.now());
        response.setValidationErrors(validationErrors);

        return response;
    }
}