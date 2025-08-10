package com.travelmate.authservice.dto;

import lombok.Builder;

@Builder
public record AuthResponse(
    boolean success,
    String message,
    String token,
    String refreshToken,
    UserInfoDTO userInfo
) {}
