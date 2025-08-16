package com.travelmate.authservice.dto;

import lombok.Builder;

@Builder
public record TokenValidationResponse(
        boolean valid,
        String userId,
        String username,
        String email,
        String role,
        String message
        ) {

}
