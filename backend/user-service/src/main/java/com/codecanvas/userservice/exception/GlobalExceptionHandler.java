//package com.codecanvas.userservice.exception;
//
//import com.codecanvas.userservice.dto.response.ApiResponse;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.RestControllerAdvice;
//
//@RestControllerAdvice
//public class GlobalExceptionHandler {
//
//    @ExceptionHandler(OtpExpiredException.class)
//    public ResponseEntity<ApiResponse> handleOtpExpiredException(OtpExpiredException ex) {
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                .body(new ApiResponse(false, ex.getMessage()));
//    }
//
//    @ExceptionHandler(InvalidOtpException.class)
//    public ResponseEntity<ApiResponse> handleInvalidOtpException(InvalidOtpException ex) {
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                .body(new ApiResponse(false, ex.getMessage()));
//    }
//
//    @ExceptionHandler(UserNotFoundException.class)
//    public ResponseEntity<ApiResponse> handleUserNotFoundException(UserNotFoundException ex) {
//        return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                .body(new ApiResponse(false, ex.getMessage()));
//    }
//
//    @ExceptionHandler(InvalidPasswordException.class)
//    public ResponseEntity<ApiResponse> handleInvalidPasswordException(InvalidPasswordException ex) {
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                .body(new ApiResponse(false, ex.getMessage()));
//    }
//
//    @ExceptionHandler(PasswordMismatchException.class)
//    public ResponseEntity<ApiResponse> handlePasswordMismatchException(PasswordMismatchException ex) {
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                .body(new ApiResponse(false, ex.getMessage()));
//    }
//
//    @ExceptionHandler(SamePasswordException.class)
//    public ResponseEntity<ApiResponse> handleSamePasswordException(SamePasswordException ex) {
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                .body(new ApiResponse(false, ex.getMessage()));
//    }
//
//    @ExceptionHandler(RuntimeException.class)
//    public ResponseEntity<ApiResponse> handleRuntimeException(RuntimeException ex) {
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                .body(new ApiResponse(false, ex.getMessage()));
//    }
//
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<ApiResponse> handleException(Exception ex) {
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                .body(new ApiResponse(false, "Something went wrong. Please try again later."));
//    }
//}


package com.codecanvas.userservice.exception;

import com.codecanvas.userservice.dto.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiResponse> handleUserNotFound(UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse(false, ex.getMessage()));
    }

    @ExceptionHandler(InvalidOtpException.class)
    public ResponseEntity<ApiResponse> handleInvalidOtp(InvalidOtpException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse(false, ex.getMessage()));
    }

    @ExceptionHandler(OtpExpiredException.class)
    public ResponseEntity<ApiResponse> handleOtpExpired(OtpExpiredException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse(false, ex.getMessage()));
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<ApiResponse> handleInvalidPassword(InvalidPasswordException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse(false, ex.getMessage()));
    }

    @ExceptionHandler(PasswordMismatchException.class)
    public ResponseEntity<ApiResponse> handlePasswordMismatch(PasswordMismatchException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse(false, ex.getMessage()));
    }

    @ExceptionHandler(SamePasswordException.class)
    public ResponseEntity<ApiResponse> handleSamePassword(SamePasswordException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse(false, ex.getMessage()));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse(false, ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse(false, "Something went wrong. Please try again later."));
    }
}