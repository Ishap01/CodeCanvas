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
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse>
    handleResourceNotFoundException(
            ResourceNotFoundException exception,
            HttpServletRequest request) {

        ErrorResponse response = createErrorResponse(
                HttpStatus.NOT_FOUND,
                getSafeMessage(
                        exception,
                        "Requested resource was not found"
                ),
                request.getRequestURI(),
                null
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }

    @ExceptionHandler(UnauthorizedActionException.class)
    public ResponseEntity<ErrorResponse>
    handleUnauthorizedActionException(
            UnauthorizedActionException exception,
            HttpServletRequest request) {

        ErrorResponse response = createErrorResponse(
                HttpStatus.FORBIDDEN,
                getSafeMessage(
                        exception,
                        "You are not allowed to perform this action"
                ),
                request.getRequestURI(),
                null
        );

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(response);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse>
    handleDuplicateResourceException(
            DuplicateResourceException exception,
            HttpServletRequest request) {

        ErrorResponse response = createErrorResponse(
                HttpStatus.CONFLICT,
                getSafeMessage(
                        exception,
                        "Resource already exists"
                ),
                request.getRequestURI(),
                null
        );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(response);
    }

    /*
     * @Valid request DTO validation errors.
     *
     * Example:
     * title blank
     * category missing
     * visibility null
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse>
    handleValidationException(
            MethodArgumentNotValidException exception,
            HttpServletRequest request) {

        Map<String, String> validationErrors =
                new LinkedHashMap<>();

        for (FieldError fieldError
                : exception.getBindingResult()
                .getFieldErrors()) {

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

    /*
     * Invalid JSON body.
     *
     * Examples:
     * malformed JSON
     * invalid enum value
     * wrong data type
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse>
    handleHttpMessageNotReadableException(
            HttpMessageNotReadableException exception,
            HttpServletRequest request) {

        logException(
                "INVALID REQUEST BODY",
                exception
        );

        ErrorResponse response = createErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Invalid request body, JSON format or enum value",
                request.getRequestURI(),
                null
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    /*
     * Multipart request mein required file field absent.
     *
     * Expected Postman form-data:
     *
     * key  : image
     * type : File
     */
    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<ErrorResponse>
    handleMissingServletRequestPartException(
            MissingServletRequestPartException exception,
            HttpServletRequest request) {

        String message =
                "Required multipart field is missing: "
                        + exception.getRequestPartName();

        ErrorResponse response = createErrorResponse(
                HttpStatus.BAD_REQUEST,
                message,
                request.getRequestURI(),
                null
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    /*
     * Normal request parameter missing.
     */
    @ExceptionHandler(
            MissingServletRequestParameterException.class
    )
    public ResponseEntity<ErrorResponse>
    handleMissingServletRequestParameterException(
            MissingServletRequestParameterException exception,
            HttpServletRequest request) {

        String message =
                "Required request parameter is missing: "
                        + exception.getParameterName();

        ErrorResponse response = createErrorResponse(
                HttpStatus.BAD_REQUEST,
                message,
                request.getRequestURI(),
                null
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    /*
     * Uploaded image configured maximum limit se badi.
     *
     * application.yml:
     *
     * max-file-size: 5MB
     * max-request-size: 5MB
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse>
    handleMaxUploadSizeExceededException(
            MaxUploadSizeExceededException exception,
            HttpServletRequest request) {

        ErrorResponse response = createErrorResponse(
                HttpStatus.PAYLOAD_TOO_LARGE,
                "Image size must not exceed 5 MB",
                request.getRequestURI(),
                null
        );

        return ResponseEntity
                .status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body(response);
    }

    /*
     * Malformed multipart/form-data request.
     *
     * Example:
     * Content-Type manually incorrect set kiya
     * multipart boundary missing hai
     */
    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<ErrorResponse>
    handleMultipartException(
            MultipartException exception,
            HttpServletRequest request) {

        logException(
                "MULTIPART REQUEST ERROR",
                exception
        );

        ErrorResponse response = createErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Invalid multipart request. Send the image using form-data with key 'image'",
                request.getRequestURI(),
                null
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    /*
     * Invalid path variable datatype.
     *
     * Example:
     * /api/snippets/abc/image
     *
     * snippetId UUID hona chahiye.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse>
    handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException exception,
            HttpServletRequest request) {

        String parameterName =
                exception.getName();

        String message;

        if (exception.getRequiredType() != null
                && exception.getRequiredType()
                .equals(java.util.UUID.class)) {

            message = parameterName
                    + " must be a valid UUID";

        } else {

            message = "Invalid value for parameter: "
                    + parameterName;
        }

        ErrorResponse response = createErrorResponse(
                HttpStatus.BAD_REQUEST,
                message,
                request.getRequestURI(),
                null
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    /*
     * Old header-based APIs ke liye.
     *
     * JWT implementation ke baad normally
     * ye handler kam use hoga.
     */
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

    /*
     * PostgreSQL constraints.
     *
     * Examples:
     * duplicate category
     * duplicate tag
     * null database column
     * foreign-key violation
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse>
    handleDataIntegrityViolationException(
            DataIntegrityViolationException exception,
            HttpServletRequest request) {

        logException(
                "DATA INTEGRITY EXCEPTION",
                exception
        );

        String message =
                "Database constraint violation";

        Throwable mostSpecificCause =
                exception.getMostSpecificCause();

        if (mostSpecificCause != null
                && mostSpecificCause.getMessage() != null
                && !mostSpecificCause.getMessage()
                .isBlank()) {

            message = mostSpecificCause
                    .getMessage();
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

    /*
     * Application validation.
     *
     * Examples:
     * empty image
     * non-image file
     * file greater than service limit
     * missing user id
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse>
    handleIllegalArgumentException(
            IllegalArgumentException exception,
            HttpServletRequest request) {

        ErrorResponse response = createErrorResponse(
                HttpStatus.BAD_REQUEST,
                getSafeMessage(
                        exception,
                        "Invalid request"
                ),
                request.getRequestURI(),
                null
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    /*
     * Application state invalid.
     *
     * Examples:
     * JWT principal invalid
     * Cloudinary invalid response
     * mapper could not create entity
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse>
    handleIllegalStateException(
            IllegalStateException exception,
            HttpServletRequest request) {

        logException(
                "ILLEGAL APPLICATION STATE",
                exception
        );

        ErrorResponse response = createErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                getSafeMessage(
                        exception,
                        "Application could not complete the request"
                ),
                request.getRequestURI(),
                null
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }

    /*
     * Remaining unexpected exceptions.
     *
     * Cloudinary upload/delete failure currently
     * RuntimeException ke through yahan aa sakta hai.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse>
    handleGeneralException(
            Exception exception,
            HttpServletRequest request) {

        logException(
                "UNEXPECTED EXCEPTION",
                exception
        );

        ErrorResponse response = createErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected server error occurred",
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

        ErrorResponse response =
                new ErrorResponse();

        response.setSuccess(false);
        response.setStatus(status.value());
        response.setError(
                status.getReasonPhrase()
        );
        response.setMessage(message);
        response.setPath(path);
        response.setTimestamp(
                LocalDateTime.now()
        );
        response.setValidationErrors(
                validationErrors
        );

        return response;
    }

    private String getSafeMessage(
            Exception exception,
            String defaultMessage) {

        if (exception == null
                || exception.getMessage() == null
                || exception.getMessage().isBlank()) {

            return defaultMessage;
        }

        return exception.getMessage();
    }

    private void logException(
            String heading,
            Exception exception) {

        System.err.println(
                "========== "
                        + heading
                        + " =========="
        );

        exception.printStackTrace();

        System.err.println(
                "=========================================="
        );
    }
}