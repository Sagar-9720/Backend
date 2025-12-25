package com.travelmate.gateway.exception;

import com.travelmate.gateway.response.CustomResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<CustomResponseEntity<Object>> handleResponseStatusException(ResponseStatusException ex, ServerWebExchange exchange) {
        String path = exchange != null ? exchange.getRequest().getPath().toString() : "unknown";
        HttpStatus status = HttpStatus.resolve(ex.getStatusCode().value());
        if (status == null) status = HttpStatus.INTERNAL_SERVER_ERROR;

        CustomResponseEntity<Object> response = CustomResponseEntity.error(
                status.value(),
                ex.getReason() != null ? ex.getReason() : ex.getMessage(),
                path
        );
        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CustomResponseEntity<Object>> handleAllExceptions(Exception ex, ServerWebExchange exchange) {
        String path = exchange != null ? exchange.getRequest().getPath().toString() : "unknown";
        CustomResponseEntity<Object> response = CustomResponseEntity.error(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                ex.getMessage(),
                path
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
