package org.orderpulse.orderpulsebackend.service;

import org.orderpulse.orderpulsebackend.dto.OrderRequest;
import org.orderpulse.orderpulsebackend.entity.Order;
import org.orderpulse.orderpulsebackend.entity.OrderStatus;
import java.util.List;

/**
 * Service interface defining business operations for orders.
 * 
 * This interface defines the contract for order management operations.
 * Implementation classes will provide the actual business logic.
 * 
 * @author OrderPulse Team
 * @version 1.0
 */
public interface OrderService {

    /**
     * Creates a new order.
     * 
     * @param orderRequest the order data
     * @return the created order
     */
    Order createOrder(OrderRequest orderRequest);

    /**
     * Retrieves an order by its ID.
     * 
     * @param id the order ID
     * @return the order
     */
    Order getOrderById(Long id);

    /**
     * Retrieves all orders.
     * 
     * @return list of all orders
     */
    List<Order> getAllOrders();

    /**
     * Updates the status of an order.
     * 
     * @param id        the order ID
     * @param newStatus the new status
     * @return the updated order
     */
    Order updateOrderStatus(Long id, OrderStatus newStatus);

    /**
     * Deletes an order.
     * 
     * @param id the order ID
     */
    void deleteOrder(Long id);

    /**
     * Finds orders by customer name.
     * 
     * @param customerName the customer name
     * @return list of orders
     */
    List<Order> getOrdersByCustomerName(String customerName);

    /**
     * Finds orders by status.
     * 
     * @param status the order status
     * @return list of orders
     */
    List<Order> getOrdersByStatus(OrderStatus status);
}