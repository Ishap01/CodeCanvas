package com.codecanvas.aiservice.security;

import java.util.UUID;

public interface JwtService {

    String extractEmail(String token);

    UUID extractUserId(String token);

    boolean isTokenValid(String token);

}