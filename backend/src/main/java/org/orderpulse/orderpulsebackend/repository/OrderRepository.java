package org.orderpulse.orderpulsebackend.repository;

import org.orderpulse.orderpulsebackend.entity.Order;
import org.orderpulse.orderpulsebackend.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for Order entity database operations.
 * 
 * Extends JpaRepository to provide:
 * - Basic CRUD operations (save, findById, findAll, delete, etc.)
 * - Pagination and sorting capabilities
 * - Custom query methods using Spring Data JPA conventions
 * 
 * @author OrderPulse Team
 * @version 1.0
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {

    /**
     * Find all orders for a specific customer.
     * Case-insensitive search using JPQL.
     * 
     * @param customerName the customer name to search for
     * @return list of orders matching the customer name
     */
    @Query("SELECT o FROM Order o WHERE LOWER(o.customerName) = LOWER(:customerName)")
    List<Order> findByCustomerNameIgnoreCase(@Param("customerName") String customerName);

    /**
     * Find all orders with a specific status.
     * Uses Spring Data JPA method naming convention.
     * 
     * @param status the order status to filter by
     * @return list of orders with the specified status
     */
    List<Order> findByStatus(OrderStatus status);

    /**
     * Find orders by customer email.
     * Case-insensitive search.
     * 
     * @param email the customer email to search for
     * @return list of orders for the given email
     */
    List<Order> findByCustomerEmailIgnoreCase(String email);

    /**
     * Find orders created within a specific date range.
     * Useful for reporting and analytics.
     * 
     * @param startDate the start of the date range (inclusive)
     * @param endDate   the end of the date range (inclusive)
     * @return list of orders created within the date range
     */
    List<Order> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find orders by customer name containing a search term.
     * Case-insensitive partial match search.
     * 
     * @param searchTerm the term to search for in customer names
     * @return list of orders with matching customer names
     */
    List<Order> findByCustomerNameContainingIgnoreCase(String searchTerm);

    /**
     * Count orders by status.
     * Useful for dashboard statistics.
     * 
     * @param status the order status to count
     * @return number of orders with the specified status
     */
    long countByStatus(OrderStatus status);

    /**
     * Check if an order exists for a given customer email and status.
     * 
     * @param email  the customer email
     * @param status the order status
     * @return true if at least one order exists, false otherwise
     */
    boolean existsByCustomerEmailAndStatus(String email, OrderStatus status);

    /**
     * Count all orders.
     *
     * @return total order count
     */
    @Query("SELECT COUNT(o) FROM Order o")
    Long countTotalOrders();

    /**
     * Sum total revenue for all orders.
     *
     * @return total revenue
     */
    @Query("SELECT SUM(o.totalPrice) FROM Order o")
    BigDecimal calculateTotalRevenue();

    /**
     * Count orders grouped by status.
     *
     * @return list of [OrderStatus, count]
     */
    @Query("SELECT o.status, COUNT(o) FROM Order o GROUP BY o.status")
    List<Object[]> countOrdersByStatus();

    /**
     * Sum revenue grouped by status.
     *
     * @return list of [OrderStatus, revenue]
     */
    @Query("SELECT o.status, SUM(o.totalPrice) FROM Order o GROUP BY o.status")
    List<Object[]> calculateRevenueByStatus();
}
