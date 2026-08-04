package com.codecanvas.searchservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSearchResponse {

    private UUID userId;

    private String fullName;

    private String username;

    private String bio;

    private String profileImage;
}