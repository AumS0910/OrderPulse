package org.orderpulse.orderpulsebackend.exception;
/**
 * Exception thrown when an order is not found in the database.
 * 
 * This is a runtime exception that will be caught by the global exception handler
 * and converted to an appropriate HTTP response (404 Not Found).
 * 
 * @author OrderPulse Team
 * @version 1.0
 */
public class OrderNotFoundException extends RuntimeException {
    
    /**
     * Constructs a new OrderNotFoundException with a detail message.
     * 
     * @param message the detail message
     */
    public OrderNotFoundException(String message) {
        super(message);
    }
    
    /**
     * Constructs a new OrderNotFoundException with order ID.
     * 
     * @param orderId the ID of the order that was not found
     */
    public OrderNotFoundException(Long orderId) {
        super("Order not found with ID: " + orderId);
    }
}