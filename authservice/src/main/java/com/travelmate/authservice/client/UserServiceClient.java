package com.travelmate.authservice.client;

import com.travelmate.authservice.config.FeignClientConfig;
import com.travelmate.authservice.dto.UserInfoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(
    name = "user-service",
    configuration = FeignClientConfig.class,
    fallbackFactory = UserServiceFallbackFactory.class
)
public interface UserServiceClient {
    @GetMapping("/api/users/email/{email}")
    UserInfoDTO getUserByEmail(@PathVariable("email") String email);

    @PostMapping("/api/users/{userId}/verify")
    boolean verifyUserCredentials(@PathVariable("userId") String userId,
                                @RequestBody UserInfoDTO credentials);

    @GetMapping("/api/users/{userId}/status")
    boolean isUserEnabled(@PathVariable("userId") String userId);
}
