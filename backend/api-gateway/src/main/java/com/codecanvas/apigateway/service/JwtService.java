package com.codecanvas.apigateway.service;

import java.util.UUID;

public interface JwtService {

    String extractEmail(String token);

    UUID extractUserId(String token);

    boolean isTokenValid(String token);

}