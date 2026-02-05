package org.orderpulse.orderpulsebackend.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for REST controllers.
 * 
 * This class intercepts exceptions thrown by controllers and converts them
 * into appropriate HTTP responses with standardized error format.
 * 
 * Benefits:
 * - Centralized error handling (DRY principle)
 * - Consistent error response format across all endpoints
 * - Proper HTTP status codes
 * - Detailed error messages for debugging
 * 
 * @author OrderPulse Team
 * @version 1.0
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Handles OrderNotFoundException.
     * Return 404 Not Found status.
     * 
     * @param ex the exception
     * @return error response
     */
    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleOrderNotFoundException(OrderNotFoundException ex) {
        log.error("Order not found: {}", ex.getMessage());

        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error("Not Found")
                .message(ex.getMessage())
                .build();

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles validation errors from @Valid annotation.
     * Returns 400 Bad Request with field-specific errors.
     * 
     * @param ex the validation exception
     * @return error response with field errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {

        Map<String, String> fieldErrors = new HashMap<>();

        // Extract field errors
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            fieldErrors.put(fieldName, errorMessage);
        });

        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Validation Failed");
        response.put("fieldErrors", fieldErrors);

        log.error("Validation failed: {}", fieldErrors);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles invalid order status exceptions.
     * Returns 400 Bad Request.
     * 
     * @param ex the exception
     * @return error response
     */
    @ExceptionHandler(InvalidOrderStatusException.class)
    public ResponseEntity<ErrorResponse> handleInvalidOrderStatusException(
            InvalidOrderStatusException ex) {

        log.error("Invalid order status: {}", ex.getMessage());

        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Bad Request")
                .message(ex.getMessage())
                .build();

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles all other unexpected exceptions.
     * Returns 500 Internal Server Error.
     * 
     * @param ex the exception
     * @return error response
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex) {
        log.error("Unexpected error occurred", ex);

        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Internal Server Error")
                .message("An unexpected error occurred. Please try again later.")
                .build();

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
