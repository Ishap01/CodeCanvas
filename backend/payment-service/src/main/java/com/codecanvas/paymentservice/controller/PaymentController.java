//package com.codecanvas.paymentservice.controller;
//
//import com.codecanvas.paymentservice.dto.request.CreateOrderRequest;
//import com.codecanvas.paymentservice.dto.request.VerifyPaymentRequest;
//import com.codecanvas.paymentservice.dto.response.ApiResponse;
//import com.codecanvas.paymentservice.dto.response.PaymentResponse;
//import com.codecanvas.paymentservice.dto.response.RazorpayOrderResponse;
//import com.codecanvas.paymentservice.service.PaymentService;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//import java.util.UUID;
//
//@RestController
//@RequestMapping("/api/payments")
//@RequiredArgsConstructor
//public class PaymentController {
//
//    private final PaymentService paymentService;
//
//    /**
//     * Create Razorpay Order
//     */
//    @PostMapping("/create-order")
//    public ResponseEntity<ApiResponse<RazorpayOrderResponse>> createOrder(
//            @Valid @RequestBody CreateOrderRequest request) {
//
//        RazorpayOrderResponse response =
//                paymentService.createOrder(request);
//
//        return ResponseEntity.status(HttpStatus.CREATED)
//                .body(
//                        ApiResponse.<RazorpayOrderResponse>builder()
//                                .success(true)
//                                .message("Razorpay order created successfully.")
//                                .data(response)
//                                .build()
//                );
//    }
//
//    /**
//     * Verify Razorpay Payment
//     */
//    @PostMapping("/verify")
//    public ResponseEntity<ApiResponse<PaymentResponse>> verifyPayment(
//            @Valid @RequestBody VerifyPaymentRequest request) {
//
//        PaymentResponse response =
//                paymentService.verifyPayment(request);
//
//        return ResponseEntity.ok(
//                ApiResponse.<PaymentResponse>builder()
//                        .success(true)
//                        .message("Payment verified successfully.")
//                        .data(response)
//                        .build()
//        );
//    }
//
//    /**
//     * Get Payment By ID
//     */
//    @GetMapping("/{paymentId}")
//    public ResponseEntity<ApiResponse<PaymentResponse>> getPaymentById(
//            @PathVariable UUID paymentId) {
//
//        PaymentResponse response =
//                paymentService.getPaymentById(paymentId);
//
//        return ResponseEntity.ok(
//                ApiResponse.<PaymentResponse>builder()
//                        .success(true)
//                        .message("Payment fetched successfully.")
//                        .data(response)
//                        .build()
//        );
//    }
//
//    /**
//     * Get Logged-in User Payment History
//     */
//    @GetMapping("/history")
//    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getMyPayments() {
//
//        List<PaymentResponse> response =
//                paymentService.getMyPayments();
//
//        return ResponseEntity.ok(
//                ApiResponse.<List<PaymentResponse>>builder()
//                        .success(true)
//                        .message("Payment history fetched successfully.")
//                        .data(response)
//                        .build()
//        );
//    }
//
//}


package com.codecanvas.paymentservice.controller;

import com.codecanvas.paymentservice.dto.request.CreateOrderRequest;
import com.codecanvas.paymentservice.dto.request.MarkPaymentFailedRequest;
import com.codecanvas.paymentservice.dto.request.VerifyPaymentRequest;
import com.codecanvas.paymentservice.dto.response.ApiResponse;
import com.codecanvas.paymentservice.dto.response.PaymentResponse;
import com.codecanvas.paymentservice.dto.response.RazorpayOrderResponse;
import com.codecanvas.paymentservice.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * Create Razorpay Order
     */
    @PostMapping("/create-order")
    public ResponseEntity<ApiResponse<RazorpayOrderResponse>> createOrder(
            @Valid @RequestBody CreateOrderRequest request) {

        RazorpayOrderResponse response =
                paymentService.createOrder(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse
                                .<RazorpayOrderResponse>builder()
                                .success(true)
                                .message(
                                        "Razorpay order created successfully."
                                )
                                .data(response)
                                .build()
                );
    }

    /**
     * Verify successful Razorpay payment.
     */
    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<PaymentResponse>> verifyPayment(
            @Valid @RequestBody VerifyPaymentRequest request) {

        PaymentResponse response =
                paymentService.verifyPayment(request);

        return ResponseEntity.ok(
                ApiResponse
                        .<PaymentResponse>builder()
                        .success(true)
                        .message(
                                "Payment verified successfully."
                        )
                        .data(response)
                        .build()
        );
    }

    /**
     * Mark an actual Razorpay checkout payment as failed.
     */
    @PostMapping("/failed")
    public ResponseEntity<ApiResponse<PaymentResponse>> markPaymentFailed(
            @Valid @RequestBody MarkPaymentFailedRequest request) {

        PaymentResponse response =
                paymentService.markPaymentFailed(request);

        return ResponseEntity.ok(
                ApiResponse
                        .<PaymentResponse>builder()
                        .success(true)
                        .message(
                                "Failed payment recorded successfully."
                        )
                        .data(response)
                        .build()
        );
    }

    /**
     * Get Payment By ID
     */
    @GetMapping("/{paymentId}")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPaymentById(
            @PathVariable UUID paymentId) {

        PaymentResponse response =
                paymentService.getPaymentById(paymentId);

        return ResponseEntity.ok(
                ApiResponse
                        .<PaymentResponse>builder()
                        .success(true)
                        .message(
                                "Payment fetched successfully."
                        )
                        .data(response)
                        .build()
        );
    }

    /**
     * Get Logged-in User Payment History
     */
    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getMyPayments() {

        List<PaymentResponse> response =
                paymentService.getMyPayments();

        return ResponseEntity.ok(
                ApiResponse
                        .<List<PaymentResponse>>builder()
                        .success(true)
                        .message(
                                "Payment history fetched successfully."
                        )
                        .data(response)
                        .build()
        );
    }
}