package com.codecanvas.notificationservice.dto.response;

import lombok.Data;

import java.util.UUID;

@Data
public class UserResponse {

    private UUID userId;

    private String fullName;

    private String username;

    private String email;

    private String bio;

    private String profileImage;
}