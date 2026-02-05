package org.orderpulse.orderpulsebackend.exception;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Standardized error response structure.
 * 
 * @author OrderPulse Team
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponse {

    /**
     * Timestamp when the error occurred.
     */
    private LocalDateTime timestamp;

    /**
     * HTTP status code.
     */
    private int status;

    /**
     * Error type/category.
     */
    private String error;

    /**
     * Detailed error message.
     */
    private String message;
}
