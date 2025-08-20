package com.travelmate.gateway.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomResponseEntity<T> {
    private int status;
    private String message;
    private T data;
    private String path;

    public static <T> CustomResponseEntity<T> success(int status, String message, T data, String path) {
        return new CustomResponseEntity<>(status, message, data, path);
    }

    public static <T> CustomResponseEntity<T> error(int status, String message, String path) {
        return new CustomResponseEntity<>(status, message, null, path);
    }
}

