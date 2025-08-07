package com.travelmate.emailservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailRequest {
    private String to;
    private String subject;
    private String link;
    private String name;
    private EmailType type;

    public enum EmailType {
        VERIFICATION,
        PASSWORD_RESET
    }
}
