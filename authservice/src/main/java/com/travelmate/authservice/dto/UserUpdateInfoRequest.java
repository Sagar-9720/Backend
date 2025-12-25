package com.travelmate.authservice.dto;

public record UserUpdateInfoRequest(
    String userId,
    String name,
    String email,
    String username,
    String bio,
    String phone,
    String dob,
    String profileImg,
    String oldPassword,
    String password
) {}
