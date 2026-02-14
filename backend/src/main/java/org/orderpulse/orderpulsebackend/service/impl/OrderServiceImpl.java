package org.orderpulse.orderpulsebackend.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.orderpulse.orderpulsebackend.dto.OrderEvent;
import org.orderpulse.orderpulsebackend.dto.OrderRequest;
import org.orderpulse.orderpulsebackend.entity.Order;
import org.orderpulse.orderpulsebackend.entity.OrderStatus;
import org.orderpulse.orderpulsebackend.exception.OrderNotFoundException;
import org.orderpulse.orderpulsebackend.kafka.OrderProducer;
import org.orderpulse.orderpulsebackend.repository.OrderRepository;
import org.orderpulse.orderpulsebackend.service.OrderService;
import org.orderpulse.orderpulsebackend.service.OrderSearchService;
import org.orderpulse.orderpulsebackend.service.InventoryService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of OrderService interface.
 * 
 * This class contains the core business logic for order management.
 * It coordinates between the repository layer (data access) and Kafka producer
 * (events).
 * 
 * Key Features:
 * - Transactional operations for data consistency
 * - Event publishing for asynchronous processing
 * - Comprehensive error handling
 * - Logging for debugging and monitoring
 * 
 * @author OrderPulse Team
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    /**
     * Repository for database operations.
     * Injected via constructor by Spring.
     */
    private final OrderRepository orderRepository;

    /**
     * Kafka producer for publishing events.
     * Injected via constructor by Spring.
     */
    private final OrderProducer orderProducer;

    /**
     * Elasticsearch search/indexing service.
     */
    private final OrderSearchService orderSearchService;

    /**
     * Inventory service used for stock reservation on order lifecycle events.
     */
    private final InventoryService inventoryService;

    /**
     * Creates a new order and publishes an ORDER_CREATED event.
     * 
     * Transaction ensures that both database save and event publishing
     * are treated as a single atomic operation.
     * 
     * @param orderRequest the order data from API
     * @return the created order with generated ID
     */
    @Override
    @Transactional
    @CacheEvict(value = {"orders", "allOrders"}, allEntries = true)
    public Order createOrder(OrderRequest orderRequest) {
        log.info("Creating new order for customer: {}", orderRequest.getCustomerName());

        // Build Order entity from DTO
        Order order = Order.builder()
                .customerName(orderRequest.getCustomerName())
                .customerEmail(orderRequest.getCustomerEmail())
                .productDescription(orderRequest.getProductDescription())
                .productSku(normalizeSku(orderRequest.getProductSku()))
                .quantity(orderRequest.getQuantity())
                .totalPrice(orderRequest.getTotalPrice())
                .status(OrderStatus.PENDING)
                .build();

        if (order.getProductSku() != null) {
            inventoryService.reserveStock(order.getProductSku(), order.getQuantity());
        }

        Order savedOrder = orderRepository.save(order);
        log.info("Order created successfully with ID: {}", savedOrder.getId());

        orderSearchService.indexOrder(savedOrder);

        // Publish event Kafka for asynchronous processing
        // Other services can react to this event (e.g., send email, update inventory)
        orderProducer.publishOrderEvent(OrderEvent.created((savedOrder)));

        return savedOrder;
    }

    /**
     * Retrieves an order by ID.
     * 
     * @param id the order ID
     * @return the order
     * @throws OrderNotFoundException if order doesn't exist
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "orders", key = "#id")
    public Order getOrderById(Long id) {
        log.debug("Fetching order with ID: {}", id);

        return orderRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Order not found with ID: {}", id);
                    return new OrderNotFoundException(id);
                });
    }

    /**
     * Retrieves all orders from the database.
     * 
     * @return list of all orders
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "allOrders")
    public List<Order> getAllOrders() {
        log.debug("Fetching all orders");
        return orderRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Order> getOrders(
            OrderStatus status,
            String customerName,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Pageable pageable) {

        Specification<Order> specification = (root, query, cb) -> cb.conjunction();

        if (status != null) {
            specification = specification.and((root, query, cb) -> cb.equal(root.get("status"), status));
        }

        if (customerName != null && !customerName.isBlank()) {
            String pattern = "%" + customerName.trim().toLowerCase() + "%";
            specification = specification.and(
                    (root, query, cb) -> cb.like(cb.lower(root.get("customerName")), pattern));
        }

        if (startDate != null) {
            specification = specification.and(
                    (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("createdAt"), startDate));
        }

        if (endDate != null) {
            specification = specification.and(
                    (root, query, cb) -> cb.lessThanOrEqualTo(root.get("createdAt"), endDate));
        }

        return orderRepository.findAll(specification, pageable);
    }

    /**
     * Updates the status of an order and publishes an ORDER_UPDATED event.
     * 
     * @param id        the order ID
     * @param newStatus the new status
     * @return the updated order
     * @throws OrderNotFoundException if order doesn't exist
     */
    @Override
    @Transactional
    @CacheEvict(value = {"orders", "allOrders"}, allEntries = true)
    public Order updateOrderStatus(Long id, OrderStatus newStatus) {
        log.info("Updating order {} status to {}", id, newStatus);

        // Fetch existing order
        Order order = getOrderById(id);

        // Store old status for logging
        OrderStatus oldStatus = order.getStatus();

        // Update status
        order.setStatus(newStatus);

        applyInventoryTransition(order, oldStatus, newStatus);

        // Save changes
        Order updatedOrder = orderRepository.save(order);
        log.info("Order {} status updated from {} to {}", id, oldStatus, newStatus);

        orderSearchService.indexOrder(updatedOrder);

        // Publish update event
        if (newStatus == OrderStatus.CANCELLED) {
            orderProducer.publishOrderEvent(OrderEvent.cancelled(updatedOrder));
        } else {
            orderProducer.publishOrderEvent(OrderEvent.updated(updatedOrder, oldStatus.name()));
        }

        return updatedOrder;
    }

    /**
     * Deletes an order by ID.
     * 
     * Note: In production, consider soft deletes instead of hard deletes
     * to maintain audit trails and enable data recovery.
     * 
     * @param id the order ID
     * @throws OrderNotFoundException if order doesn't exist
     */
    @Override
    @Transactional
    @CacheEvict(value = {"orders", "allOrders"}, allEntries = true)
    public void deleteOrder(Long id) {
        log.info("Deleting order with ID: {}", id);

        // Verify order exists
        Order order = getOrderById(id);
        if (order.getProductSku() != null
                && order.getStatus() != OrderStatus.CANCELLED
                && order.getStatus() != OrderStatus.DELIVERED) {
            inventoryService.releaseReservation(order.getProductSku(), order.getQuantity());
        }

        // Delete from database
        orderRepository.delete(order);
        log.info("Order {} deleted successfully", id);

        orderSearchService.deleteOrder(id);

        // Optionally publish deletion event
        // orderProducer.publishOrderEvent(OrderEvent.deleted(order));
    }

    /**
     * Finds all orders for a specific customer.
     * 
     * @param customerName the customer name (case-insensitive)
     * @return list of orders
     */
    @Override
    @Transactional(readOnly = true)
    public List<Order> getOrdersByCustomerName(String customerName) {
        log.debug("Fetching orders for customer: {}", customerName);
        return orderRepository.findByCustomerNameIgnoreCase(customerName);
    }

    /**
     * Finds all orders with a specific status.
     * 
     * @param status the order status
     * @return list of orders
     */
    @Override
    @Transactional(readOnly = true)
    public List<Order> getOrdersByStatus(OrderStatus status) {
        log.debug("Fetching orders with status: {}", status);
        return orderRepository.findByStatus(status);
    }

    private String normalizeSku(String sku) {
        if (sku == null || sku.isBlank()) {
            return null;
        }
        return sku.trim().toUpperCase();
    }

    private void applyInventoryTransition(Order order, OrderStatus oldStatus, OrderStatus newStatus) {
        if (order.getProductSku() == null || oldStatus == newStatus) {
            return;
        }

        if (newStatus == OrderStatus.CANCELLED && oldStatus != OrderStatus.CANCELLED) {
            inventoryService.releaseReservation(order.getProductSku(), order.getQuantity());
            return;
        }

        if (newStatus == OrderStatus.DELIVERED && oldStatus != OrderStatus.DELIVERED) {
            inventoryService.consumeReservation(order.getProductSku(), order.getQuantity());
        }
    }
}
