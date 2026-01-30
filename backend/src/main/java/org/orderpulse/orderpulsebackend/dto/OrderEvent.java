package org.orderpulse.orderpulsebackend.dto;

import lombok.*;
import org.orderpulse.orderpulsebackend.entity.Order;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for Order events published to Kafka.
 * 
 * This class represents the structure of messages sent to Kafka topics.
 * It contains the event type, timestamp, and the complete order data.
 * 
 * @author OrderPulse Team
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderEvent {

    /**
     * Type of event that occurred.
     * Examples: ORDER_CREATED, ORDER_UPDATED, ORDER_CANCELLED
     */
    private String eventType;

    /**
     * Timestamp when the event was generated.
     * Used for event ordering and debugging.
     */
    private LocalDateTime timestamp;

    /**
     * The complete order data associated with this event.
     * Consumers can access all order information from this field.
     */
    private Order order;

    /**
     * Optional message providing additional context about the event.
     * Example: "Order status changed from PENDING to CONFIRMED"
     */
    private String message;

    /**
     * Factory method to create an ORDER_CREATED event.
     * 
     * @param order the newly created order
     * @return OrderEvent with type ORDER_CREATED
     */
    public static OrderEvent created(Order order) {
        return OrderEvent.builder()
                .eventType("ORDER_CREATED")
                .timestamp(LocalDateTime.now())
                .order(order)
                .message("New order created for customer: " + order.getCustomerName())
                .build();
    }

    /**
     * Factory method to create an ORDER_UPDATED event.
     * 
     * @param order the updated order
     * @return OrderEvent with type ORDER_UPDATED
     */
    public static OrderEvent updated(Order order) {
        return OrderEvent.builder()
                .eventType("ORDER_UPDATED")
                .timestamp(LocalDateTime.now())
                .order(order)
                .message("Order updated: " + order.getId())
                .build();
    }

    /**
     * Factory method to create an ORDER_CANCELLED event.
     * 
     * @param order the cancelled order
     * @return OrderEvent with type ORDER_CANCELLED
     */
    public static OrderEvent cancelled(Order order) {
        return OrderEvent.builder()
                .eventType("ORDER_CANCELLED")
                .timestamp(LocalDateTime.now())
                .order(order)
                .message("Order cancelled: " + order.getId())
                .build();
    }
}