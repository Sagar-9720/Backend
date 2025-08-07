package com.travelmate.tripservice.dto;

import lombok.Data;

@Data
public class UserDetailsDTO {
    private String id;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
}
