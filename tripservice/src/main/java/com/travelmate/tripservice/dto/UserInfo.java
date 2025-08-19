package com.travelmate.tripservice.dto;

import lombok.Builder;

@Builder
public record UserInfo(String userId, String username, String role, String email) {
}
