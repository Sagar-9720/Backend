package com.travelmate.journalservice.exceptions;

import com.travelmate.journalservice.response.CustomResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ========== Authentication & Authorization Exceptions ==========
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<CustomResponseEntity<?>> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(CustomResponseEntity.error(403, "Access denied: " + ex.getMessage(), "/auth"));
    }




    // ========== Validation Exception ==========
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CustomResponseEntity<?>> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(err -> {
            String field = ((FieldError) err).getField();
            String message = err.getDefaultMessage();
            errors.put(field, message);
        });
        return ResponseEntity.badRequest().body(CustomResponseEntity.error(400, "Validation Failed: " + errors.toString(), "/validation"));
    }


    @ExceptionHandler(TravelJournalNotFoundException.class)
    public ResponseEntity<CustomResponseEntity<?>> handleTravelJournalNotFound(TravelJournalNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(CustomResponseEntity.error(404, ex.getMessage(), "/api/traveljournals"));
    }

    @ExceptionHandler(UnauthorizedAccessException.class)
    public ResponseEntity<CustomResponseEntity<?>> handleUnauthorizedAccess(UnauthorizedAccessException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(CustomResponseEntity.error(401, ex.getMessage(), "/unauthorized"));
    }

    // ========== Generic / Catch-all ==========
    @ExceptionHandler(Exception.class)
    public ResponseEntity<CustomResponseEntity<?>> handleAllUncaught(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON).body(CustomResponseEntity.error(500, ex.getMessage(), "unknown"));
    }


}
