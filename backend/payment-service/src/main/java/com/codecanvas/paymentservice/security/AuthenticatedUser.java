package com.codecanvas.paymentservice.security;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class AuthenticatedUser {

    private UUID userId;

    private String email;

}