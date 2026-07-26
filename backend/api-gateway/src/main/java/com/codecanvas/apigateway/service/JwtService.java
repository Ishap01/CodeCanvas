package com.codecanvas.apigateway.service;

public interface JwtService {

    String extractEmail(String token);

    boolean isTokenValid(String token);

}