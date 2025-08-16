package com.travelmate.journalservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.travelmate.journalservice.client.AuthServiceClient;
import com.travelmate.journalservice.client.TokenValidationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
            tokenValidationCache.put(token, response);
            return response.isValid();
        } catch (Exception e) {
            logger.warn("Exception during token validation: {}", e.getMessage());
            tokenValidationCache.put(token, null);
            return false;
        }
    }

    public String getRole(String token) throws JsonProcessingException {
        logger.info("Checking role for token: {}", token);
        String cachedResponse = tokenValidationCache.get(token);
        if (cachedResponse != null) {
            logger.info("Role has been cached for token: {}", token);
            TokenValidationResponse response = new ObjectMapper().readValue(cachedResponse, TokenValidationResponse.class);
            return response.getRole();
        }
        try {
            TokenValidationResponse response = authServiceClient.validateTokenExtracted(token);
            logger.info("TokenValidationResponse extracted: {}", response);
            if (response.isValid()) {
                tokenValidationCache.put(token, response);
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

    public String getUserId(String token) throws JsonProcessingException {
        logger.info("Checking user ID for token: {}", token);
        String cachedResponse = tokenValidationCache.get(token);
        if (cachedResponse != null) {
            logger.info("User ID has been cached for token: {}", token);
            TokenValidationResponse response = new ObjectMapper().readValue(cachedResponse, TokenValidationResponse.class);
            return response.getUserId();
        }
        try {
            TokenValidationResponse response = authServiceClient.validateTokenExtracted(token);
            logger.info("TokenValidationResponse extracted: {}", response);
            if (response.isValid()) {
                tokenValidationCache.put(token, response);
                return response.getUserId();
            } else {
                tokenValidationCache.put(token, null);
                return null;
            }
        } catch (Exception e) {
            logger.warn("Exception during user ID retrieval: {}", e.getMessage());
            tokenValidationCache.put(token, null);
            return null;
        }
    }
}
