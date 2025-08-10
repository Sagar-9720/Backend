package com.travelmate.authservice.dto;

import lombok.Builder;

@Builder
public record LogoutResponse(
    String name,
    String message
) {}
