package com.codecanvas.userservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PublicProfileResponse {

    private UUID userId;

    private String fullName;

    private String username;

    private String profileImage;

    private String bio;

    private LocalDateTime createdAt;

    private boolean following;

    private boolean ownProfile;
}