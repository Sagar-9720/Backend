package com.travelmate.tripservice.service;

import com.travelmate.tripservice.client.AuthServiceClient;
import com.travelmate.tripservice.client.TokenValidationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class TokenValidationService {
    @Autowired
    private AuthServiceClient authServiceClient;
    @Autowired
    private TokenValidationCache tokenValidationCache;

    private static Logger logger = LoggerFactory.getLogger(TokenValidationService.class);

    public boolean isTokenValid(String token) {
        String cachedRole = tokenValidationCache.get(token);
        logger.info("Checking token validity for: {}", token);
        if (cachedRole != null) {
            logger.info("Token validity has been cached for: {}", token);
            return true;
        }
        try {
            TokenValidationResponse response = authServiceClient.validateTokenExtracted(token);
            logger.info("TokenValidationResponse extracted: {}", response);
            tokenValidationCache.put(token, response.getRole());
            return response.isValid();
        } catch (Exception e) {
            logger.warn("Exception during token validation: {}", e.getMessage());
            tokenValidationCache.put(token, null);
            return false;
        }
    }

    public String getRole(String token) {
        String cachedRole = tokenValidationCache.get(token);
        logger.info("Checking role for token: {}", token);
        if (cachedRole != null) {
            logger.info("Role has been cached for token: {}", token);
            return cachedRole;
        }
        try {
            TokenValidationResponse response = authServiceClient.validateTokenExtracted(token);
            logger.info("TokenValidationResponse extracted: {}", response);
            if (response.isValid()) {
                tokenValidationCache.put(token, response.getRole());
                return response.getRole();
            } else {
                tokenValidationCache.put(token, null);
                return null;
            }
        } catch (Exception e) {
            logger.warn("Exception during role retrieval: {}", e.getMessage());
            tokenValidationCache.put(token, null);
            return null;
        }
    }
}
