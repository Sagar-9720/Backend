package com.travelmate.tripservice.client;

import com.travelmate.tripservice.config.FeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@FeignClient(name = "AUTHSERVICE", configuration = FeignClientConfig.class, fallback = AuthServiceClientFallback.class)
public interface AuthServiceClient {
    @PostMapping("/api/auth/validate")
    @CircuitBreaker(name = "authService", fallbackMethod = "validateTokenFallback")
    TokenValidationResponse validateToken(@RequestHeader("Authorization") String token);

}
