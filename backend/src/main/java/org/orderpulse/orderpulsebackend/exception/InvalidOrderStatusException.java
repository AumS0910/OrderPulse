package org.orderpulse.orderpulsebackend.exception;

/**
 * Exception thrown when an invalid status transition is attempted.
 * 
 * @author OrderPulse Team
 * @version 1.0
 */
public class InvalidOrderStatusException extends RuntimeException {

    public InvalidOrderStatusException(String message) {
        super(message);
    }
}