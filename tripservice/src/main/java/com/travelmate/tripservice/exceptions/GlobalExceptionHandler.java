package com.travelmate.tripservice.exceptions;

import com.travelmate.tripservice.response.CustomResponseEntity;
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

    // ========== Destination Exceptions ==========
    @ExceptionHandler(DestinationNotFoundException.class)
    public ResponseEntity<CustomResponseEntity<?>> handleDestinationNotFound(DestinationNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(CustomResponseEntity.error(404, ex.getMessage(), "/api/destinations"));
    }

    @ExceptionHandler(DestinationExistException.class)
    public ResponseEntity<CustomResponseEntity<?>> handleDestinationExist(DestinationExistException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(CustomResponseEntity.error(409, ex.getMessage(), "/api/destinations"));
    }

    // ========== Trip Exceptions ==========
    @ExceptionHandler(TripNotFoundException.class)
    public ResponseEntity<CustomResponseEntity<?>> handleTripNotFound(TripNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(CustomResponseEntity.error(404, ex.getMessage(), "/api/trips"));
    }

    @ExceptionHandler(TripExistsException.class)
    public ResponseEntity<CustomResponseEntity<?>> handleTripExist(TripExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(CustomResponseEntity.error(409, ex.getMessage(), "/api/trips"));
    }


    // ========== Itinerary Exceptions ==========
    @ExceptionHandler(ItineraryNotFoundException.class)
    public ResponseEntity<CustomResponseEntity<?>> handleItineraryNotFound(ItineraryNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(CustomResponseEntity.error(404, ex.getMessage(), "/api/itineraries"));
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

    @ExceptionHandler(CountryNotFoundException.class)
    public ResponseEntity<CustomResponseEntity<?>> handleCountryNotFound(CountryNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(CustomResponseEntity.error(404, ex.getMessage(), "/api/countries"));
    }

    @ExceptionHandler(RegionNotFoundException.class)
    public ResponseEntity<CustomResponseEntity<?>> handleRegionNotFound(RegionNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(CustomResponseEntity.error(404, ex.getMessage(), "/api/regions"));
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
