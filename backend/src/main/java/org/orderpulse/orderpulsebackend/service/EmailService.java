package org.orderpulse.orderpulsebackend.service;

import org.orderpulse.orderpulsebackend.entity.Order;

/**
 * Service interface for sending emails.
 * 
 * @author OrderPulse Team
 * @version 1.0
 */
public interface EmailService {

    /**
     * Send order confirmation email to customer.
     * 
     * @param order the order details
     */
    void sendOrderConfirmationEmail(Order order);

    /**
     * Send order status update email to customer.
     * 
     * @param order     the order with updated status
     * @param oldStatus the previous status
     */
    void sendOrderStatusUpdateEmail(Order order, String oldStatus);

    /**
     * Send order cancellation email to customer.
     * 
     * @param order the cancelled order
     */
    void sendOrderCancellationEmail(Order order);
}
