package com.travelmate.tripservice.service;

import com.travelmate.tripservice.client.AuthServiceClient;
import com.travelmate.tripservice.client.TokenValidationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TokenValidationService {
    @Autowired
    private AuthServiceClient authServiceClient;
    @Autowired
    private TokenValidationCache tokenValidationCache;

    public boolean isTokenValid(String token) {
        Boolean cached = tokenValidationCache.get(token);
        if (cached != null) {
            return cached;
        }
        try {
            TokenValidationResponse response = authServiceClient.validateToken(token);
            boolean isValid = response != null && response.isValid();
            tokenValidationCache.put(token, isValid);
            return isValid;
        } catch (Exception e) {
            tokenValidationCache.put(token, false);
            return false;
        }
    }
}

