package com.travelmate.tripservice.client;

import org.springframework.stereotype.Component;

@Component
public class AuthServiceClientFallback implements AuthServiceClient {
    @Override
    public TokenValidationResponse validateToken(String token) {
        return null; // or provide a default TokenValidationResponse if needed
    }
}

