package com.travelmate.authservice.dto;

import lombok.*;

@Data
@Getter
@Setter
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
