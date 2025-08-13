package com.travelmate.tripservice.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomResponseEntity<T> {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
    private int status;
    private String message;
    private T data;
    private String path;

    public static <T> CustomResponseEntity<T> success(int status, String message, T data, String path) {
        return CustomResponseEntity.<T>builder()
                .status(status)
                .message(message)
                .data(data)
                .path(path)
                .build();
    }

    public static <T> CustomResponseEntity<T> error(int status, String message, String path) {
        return CustomResponseEntity.<T>builder()
                .status(status)
                .message(message)
                .path(path)
                .build();
    }
}
