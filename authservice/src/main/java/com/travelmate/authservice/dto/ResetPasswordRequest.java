package com.travelmate.authservice.dto;

public record ResetPasswordRequest(
    String token,
    String password
) {}

