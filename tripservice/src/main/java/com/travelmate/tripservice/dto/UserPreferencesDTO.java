package com.travelmate.tripservice.dto;

import lombok.Data;

@Data
public class UserPreferencesDTO {
    private String userId;
    private String preferredCurrency;
    private String preferredLanguage;
    private String travelStyle;
    private boolean emailNotifications;
    private boolean pushNotifications;
}
