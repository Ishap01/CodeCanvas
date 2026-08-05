package com.codecanvas.notificationservice.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdatedEvent {

    private UUID userId;

    private String fullName;

    private String username;

    private String email;

    private String bio;

    private String profileImage;
}
