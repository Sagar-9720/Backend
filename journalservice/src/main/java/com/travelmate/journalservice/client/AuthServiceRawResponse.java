package com.travelmate.journalservice.client;

import lombok.Data;

@Data
public class AuthServiceRawResponse {
    private Object timestamp;
    private int status;
    private String message;
    private Object data;
    private String path;
}
