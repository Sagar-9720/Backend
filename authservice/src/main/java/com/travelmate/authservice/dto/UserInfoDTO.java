package com.travelmate.authservice.dto;

import lombok.Builder;

@Builder
public record UserInfoDTO(
    String userId,
    String name,
    String email,
    String phone,
    String dob,
    String gender,
    String profileImg,
    String role
) {}
