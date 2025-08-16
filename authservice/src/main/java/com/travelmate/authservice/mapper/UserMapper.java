package com.travelmate.authservice.mapper;

import com.travelmate.authservice.dto.UserInfoDTO;
import com.travelmate.authservice.entity.User;

public record UserMapper() {
    public static UserInfoDTO toUserInfoDTO(User user) {
        if (user == null) return null;
        return UserInfoDTO.builder()
                .userId(user.getUserId() != null ? user.getUserId().toString() : null)
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .dob(user.getDob() != null ? user.getDob().toString() : null)
                .gender(user.getGender() != null ? user.getGender().toString() : null)
                .profileImg(user.getProfileImg() != null ? user.getProfileImg() : "")
                .role(user.getRole() != null ? user.getRole().toString() : null)
                .build();

    }
}
