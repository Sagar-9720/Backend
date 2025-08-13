package com.travelmate.tripservice.client;

import lombok.*;

@Data
@Builder
@Getter
@AllArgsConstructor
public class TokenValidationResponse {
    private boolean valid;
    private String userId;
    private String username;
    private String email;
    private String role;
    private String message;

}