package com.travelmate.journalservice.client;

import org.springframework.stereotype.Component;

@Component
public class AuthServiceClientFallback implements AuthServiceClient {
    @Override
    public AuthServiceRawResponse validateToken(String token) {
        AuthServiceRawResponse raw = new AuthServiceRawResponse();
        raw.setData(TokenValidationResponse.builder().valid(false).message("Token validation failed: no data").build());
        return raw;
    }

    public TokenValidationResponse validateTokenFallback(String token, Throwable t) {
        // Custom fallback for Resilience4j circuit breaker
        return TokenValidationResponse.builder().valid(false).userId(null).username(null).email(null).role(null).message("Token validation failed: fallback invoked.").build();
    }
}
