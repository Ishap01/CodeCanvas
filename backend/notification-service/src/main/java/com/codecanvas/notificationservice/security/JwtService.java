package com.codecanvas.notificationservice.security;

import java.util.UUID;

public interface JwtService {

    String extractEmail(String token);

    UUID extractUserId(String token);

    boolean isTokenValid(String token);

}