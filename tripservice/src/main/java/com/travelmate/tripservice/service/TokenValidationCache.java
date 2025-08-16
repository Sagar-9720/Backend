package com.travelmate.tripservice.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.travelmate.tripservice.client.TokenValidationResponse;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class TokenValidationCache {
    private final Cache<String, String> cache = Caffeine.newBuilder().expireAfterWrite(2, TimeUnit.MINUTES).maximumSize(10000).build();

    public String get(String token) {
        return cache.getIfPresent(token);
    }

    public void put(String token, TokenValidationResponse response) {
        cache.put(token, response.toString());
    }

}

