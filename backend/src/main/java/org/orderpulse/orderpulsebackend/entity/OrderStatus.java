package org.orderpulse.orderpulsebackend.entity;

/**
 * Enumeration representing the various states an order can be in throughout its
 * lifecycle.
 * 
 * This enum is used to track order progression from creation to completion or
 * cancellation.
 * Each status represents a distinct stage in the order fulfillment process.
 * 
 * @author OrderPulse Team
 * @version 1.0
 */
public enum OrderStatus {

    /**
     * Initial state when an order is created but not yet confirmed.
     * The order is awaiting payment or initial validation.
     */
    PENDING,

    /**
     * Order has been confirmed and payment has been received.
     * The order is ready to be processed for fulfillment.
     */
    CONFIRMED,

    /**
     * Order is currently being prepared or assembled.
     * Items are being picked, packed, or manufactured.
     */
    PROCESSING,

    /**
     * Order has been dispatched and is in transit to the customer.
     * Tracking information should be available at this stage.
     */
    SHIPPED,

    /**
     * Order has been successfully delivered to the customer.
     * This is a terminal state for successful orders.
     */
    DELIVERED,

    /**
     * Order has been cancelled by the customer or system.
     * This is a terminal state. Refund processing may be initiated.
     */
    CANCELLED
}