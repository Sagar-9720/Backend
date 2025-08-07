package com.travelmate.authservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserUpdateInfoRequest {

    private String userId;
    private String name;
    private String email;
    private String phone;
    private String dob;
    private String profileImg;
    private String token;
    private String oldPassword;
    private String password;
}
