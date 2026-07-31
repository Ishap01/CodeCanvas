package com.codecanvas.snippetservice.security;

import java.util.UUID;

public class AuthenticatedUser {

    private final UUID userId;
    private final String subject;

    public AuthenticatedUser(
            UUID userId,
            String subject) {

        this.userId = userId;
        this.subject = subject;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getSubject() {
        return subject;
    }
}