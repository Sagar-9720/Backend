package com.travelmate.tripservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.travelmate.tripservice.dto.UserInfo;

public class ExtractHeader {
    public static UserInfo extractHeader(String authHeader) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(authHeader, UserInfo.class);
    }
}
