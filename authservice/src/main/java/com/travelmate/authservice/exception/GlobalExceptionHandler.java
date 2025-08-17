package com.travelmate.authservice.exception;

import com.travelmate.authservice.response.CustomResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmailAlreadyExistException.class)
    public ResponseEntity<CustomResponseEntity<Object>> handleEmailAlreadyExist(EmailAlreadyExistException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                CustomResponseEntity.error(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), request.getRequestURI())
        );
    }

    @ExceptionHandler(EmailNotFoundException.class)
    public ResponseEntity<CustomResponseEntity<Object>> handleEmailNotFound(EmailNotFoundException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                CustomResponseEntity.error(HttpStatus.NOT_FOUND.value(), ex.getMessage(), request.getRequestURI())
        );
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<CustomResponseEntity<Object>> handleUserNotFound(UserNotFoundException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                CustomResponseEntity.error(HttpStatus.NOT_FOUND.value(), ex.getMessage(), request.getRequestURI())
        );
    }

    @ExceptionHandler(UnauthorizedAccessException.class)
    public ResponseEntity<CustomResponseEntity<Object>> handleUnauthorized(UnauthorizedAccessException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                CustomResponseEntity.error(HttpStatus.UNAUTHORIZED.value(), ex.getMessage(), request.getRequestURI())
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleAll(Exception ex, HttpServletRequest request) {
        String contentType = request.getHeader("Accept");
        String uri = request.getRequestURI();
        // If the request is for Prometheus/OpenMetrics, return plain text error
        if (uri.contains("prometheus") || (contentType != null && contentType.contains("openmetrics-text"))) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(org.springframework.http.MediaType.TEXT_PLAIN)
                    .body("Internal server error");
        }
        // Default: return your custom JSON error
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                CustomResponseEntity.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage(), uri)
        );
    }
}
