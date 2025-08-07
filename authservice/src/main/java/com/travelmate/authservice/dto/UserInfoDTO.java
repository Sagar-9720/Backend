package com.travelmate.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Setter;

@Data
@Builder
@Setter
@AllArgsConstructor
public class UserInfoDTO {
    private String userId;
    private String name;
    private String email;
    private String phone;
    private String dob;
    private String gender;
    private String profileImg;
    private String role;
}
