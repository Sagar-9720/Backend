package com.travelmate.authservice.dto;

import lombok.Builder;

@Builder
public record EmailRequest(
        String to,
        String subject,
        String link,
        String name,
        EmailType type
) {
    public enum EmailType {
        VERIFICATION,
        PASSWORD_RESET
    }
}
