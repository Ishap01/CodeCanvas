//package com.codecanvas.notificationservice.controller;
//
//import com.codecanvas.notificationservice.dto.request.CreateNotificationRequest;
//import com.codecanvas.notificationservice.dto.response.ApiResponse;
//import com.codecanvas.notificationservice.dto.response.NotificationResponse;
//import com.codecanvas.notificationservice.security.AuthenticatedUser;
//import com.codecanvas.notificationservice.service.NotificationService;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.security.core.Authentication;
//
//import java.util.List;
//import java.util.UUID;
//
//@RestController
//@RequestMapping("/api/notifications")
//@RequiredArgsConstructor
//public class NotificationController {
//
//    private final NotificationService notificationService;
//
//    @PostMapping
//    public ApiResponse createNotification(
//            @Valid @RequestBody CreateNotificationRequest request) {
//
//        return notificationService.createNotification(request);
//    }
//
//    @GetMapping
//    public List<NotificationResponse> getNotifications(
//            Authentication authentication) {
//
//        AuthenticatedUser user =
//                (AuthenticatedUser) authentication.getPrincipal();
//
//        return notificationService.getNotifications(
//                user.userId()
//        );
//    }
//
//    @PutMapping("/{notificationId}/read")
//    public ApiResponse markAsRead(
//            @PathVariable UUID notificationId) {
//
//        return notificationService.markAsRead(notificationId);
//    }
//
//    @DeleteMapping("/{notificationId}")
//    public ApiResponse deleteNotification(
//            @PathVariable UUID notificationId) {
//
//        return notificationService.deleteNotification(notificationId);
//    }
//
//    @GetMapping("/unread-count")
//    public long getUnreadCount(
//            Authentication authentication) {
//
//        AuthenticatedUser user =
//                (AuthenticatedUser) authentication.getPrincipal();
//
//        return notificationService.getUnreadCount(
//                user.userId()
//        );
//    }
//
//}


package com.codecanvas.notificationservice.controller;

import com.codecanvas.notificationservice.dto.request.CreateNotificationRequest;
import com.codecanvas.notificationservice.dto.response.ApiResponse;
import com.codecanvas.notificationservice.dto.response.NotificationResponse;
import com.codecanvas.notificationservice.security.AuthenticatedUser;
import com.codecanvas.notificationservice.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping
    public ApiResponse createNotification(
            @Valid @RequestBody CreateNotificationRequest request) {

        return notificationService.createNotification(request);
    }

    @GetMapping
    public List<NotificationResponse> getNotifications(Authentication authentication) {

        System.out.println("====================================");
        System.out.println("Authentication = " + authentication);

        AuthenticatedUser user =
                (AuthenticatedUser) authentication.getPrincipal();

        System.out.println("JWT User ID = " + user.userId());
        System.out.println("JWT Email = " + user.email());
        System.out.println("====================================");

        return notificationService.getNotifications(user.userId());
    }

    @PutMapping("/{notificationId}/read")
    public ApiResponse markAsRead(
            @PathVariable UUID notificationId) {

        return notificationService.markAsRead(notificationId);
    }

    @DeleteMapping("/{notificationId}")
    public ApiResponse deleteNotification(
            @PathVariable UUID notificationId) {

        return notificationService.deleteNotification(notificationId);
    }

    @GetMapping("/unread-count")
    public long getUnreadCount(
            Authentication authentication) {

        AuthenticatedUser user =
                (AuthenticatedUser) authentication.getPrincipal();

        return notificationService.getUnreadCount(
                user.userId()
        );
    }

}