package com.travelmate.tripservice.client;

import com.travelmate.tripservice.config.FeignClientConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@FeignClient(name = "AUTHSERVICE", configuration = FeignClientConfig.class, fallback = AuthServiceClientFallback.class)
public interface AuthServiceClient {

    Logger logger = LoggerFactory.getLogger(AuthServiceClient.class);

    @PostMapping("/api/auth/validate")
    @CircuitBreaker(name = "authService", fallbackMethod = "validateTokenExtractedFallback")
    AuthServiceRawResponse validateToken(@RequestHeader("Authorization") String authHeader);

    default TokenValidationResponse validateTokenExtracted(String authHeader) {
        AuthServiceRawResponse raw = validateToken(authHeader);
        logger.info("AuthServiceRawResponse received: {}", raw);
        if (raw != null && raw.getData() != null) {
            logger.info("Data extracted from AuthServiceRawResponse: {}", raw.getData());
            Object data = raw.getData();
            if (data instanceof TokenValidationResponse) {
                logger.info("Data is already a TokenValidationResponse: {}", data);
                return (TokenValidationResponse) data;
            } else if (data instanceof java.util.Map) {
                logger.info("Data is a Map, converting to TokenValidationResponse: {}", data);
                java.util.Map<?, ?> map = (java.util.Map<?, ?>) data;
                return TokenValidationResponse.builder().valid(Boolean.TRUE.equals(map.get("valid"))).userId(map.get("userId") != null ? map.get("userId").toString() : null).username(map.get("username") != null ? map.get("username").toString() : null).email(map.get("email") != null ? map.get("email").toString() : null).role(map.get("role") != null ? map.get("role").toString() : null).message(map.get("message") != null ? map.get("message").toString() : null).build();
            }
        }
        logger.warn("Token validation failed: no valid data in response");
        return TokenValidationResponse.builder().valid(false).message("Token validation failed: no data").build();
    }

    default TokenValidationResponse validateTokenExtractedFallback(String token, Throwable t) {
        logger.error("Fallback method invoked for token validation due to: {}", t.getMessage());
        return TokenValidationResponse.builder().valid(false).userId(null).username(null).email(null).role(null).message("Token validation failed: fallback invoked.").build();
    }
}
