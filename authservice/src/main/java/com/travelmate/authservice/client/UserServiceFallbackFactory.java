package com.travelmate.authservice.client;

import com.travelmate.authservice.dto.UserInfoDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class UserServiceFallbackFactory implements FallbackFactory<UserServiceClient> {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceFallbackFactory.class);

    @Override
    public UserServiceClient create(Throwable cause) {
        return new UserServiceClient() {
            @Override
            public UserInfoDTO getUserByEmail(String email) {
                logger.error("Error getting user by email: {}", email, cause);
                return null;
            }

            @Override
            public boolean verifyUserCredentials(String userId, UserInfoDTO credentials) {
                logger.error("Error verifying credentials for user: {}", userId, cause);
                return false;
            }

            @Override
            public boolean isUserEnabled(String userId) {
                logger.error("Error checking user status: {}", userId, cause);
                return false;
            }
        };
    }
}
