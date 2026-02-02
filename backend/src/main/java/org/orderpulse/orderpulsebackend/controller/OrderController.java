package org.orderpulse.orderpulsebackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.orderpulse.orderpulsebackend.dto.OrderRequest;
import org.orderpulse.orderpulsebackend.dto.OrderResponse;
import org.orderpulse.orderpulsebackend.dto.OrderStatusUpdateRequest;
import org.orderpulse.orderpulsebackend.entity.Order;
import org.orderpulse.orderpulsebackend.entity.OrderStatus;
import org.orderpulse.orderpulsebackend.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller for order management operations.
 * 
 * This controller exposes RESTful endpoints for creating, reading, updating,
 * and deleting orders. It follows REST best practices and includes
 * comprehensive
 * API documentation using OpenAPI/Swagger annotations.
 * 
 * Base URL: /api/orders
 * 
 * @author OrderPulse Team
 * @version 1.0
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Order Management", description = "APIs for managing orders")
public class OrderController {

    /**
     * Order service for business logic.
     * Injected via constructor.
     */
    private final OrderService orderService;

    /**
     * Creates a new order.
     * 
     * @param orderRequest the order data
     * @return created order with 201 Created status
     */
    @PostMapping
    @Operation(summary = "Create a new order", description = "Creates a new order and returns the created order details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Order created successfully", content = @Content(schema = @Schema(implementation = OrderResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderRequest orderRequest) {

        log.info("Received request to create order for customer: {}", orderRequest.getCustomerName());

        Order createdOrder = orderService.createOrder(orderRequest);
        OrderResponse response = OrderResponse.fromEntity(createdOrder);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Retrieves an order by ID.
     * 
     * @param id the order ID.
     * @return order details with 200 OK status
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get order by ID", description = "Retrieves order details for the specified ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order found", content = @Content(schema = @Schema(implementation = OrderResponse.class))),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<OrderResponse> getOrderById(
            @Parameter(description = "Order ID") @PathVariable Long id) {

        log.info("Received request to get order with ID: {}", id);

        Order order = orderService.getOrderById(id);
        OrderResponse response = OrderResponse.fromEntity(order);

        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves all orders.
     * 
     * @return list of all orders with 200 OK status
     */
    @GetMapping
    @Operation(summary = "Get all orders", description = "Retrieves a list of all orders")
    @ApiResponse(responseCode = "200", description = "Orders retrieved successfully")
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        log.info("Received request to get all orders");

        List<Order> orders = orderService.getAllOrders();
        List<OrderResponse> responses = orders.stream()
                .map(OrderResponse::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    /**
     * Updates order status.
     * 
     * @param id           the order ID
     * @param statusUpdate the new status
     * @return updated order with 200 OK status
     */
    @PutMapping("/{id}/status")
    @Operation(summary = "Update order status", description = "Updates the status of an existing order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order status updated successfully"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "400", description = "Invalid status")
    })
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @Parameter(description = "Order ID") @PathVariable Long id,
            @Valid @RequestBody OrderStatusUpdateRequest statusUpdate) {

        log.info("Received request to update order {} status to {}",
                id, statusUpdate.getStatus());

        Order updatedOrder = orderService.updateOrderStatus(id, statusUpdate.getStatus());
        OrderResponse response = OrderResponse.fromEntity(updatedOrder);

        return ResponseEntity.ok(response);
    }

    /**
     * Deletes an order.
     * 
     * @param id the order ID
     * @return 204 No Content status
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete order", description = "Deletes an order by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Order deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<Void> deleteOrder(
            @Parameter(description = "Order ID") @PathVariable Long id) {

        log.info("Received request to delete order with ID: {}", id);

        orderService.deleteOrder(id);

        return ResponseEntity.noContent().build();
    }

    /**
     * Finds orders by customer name.
     * 
     * @param customerName the customer name
     * @return list of orders with 200 OK status
     */
    @GetMapping("/customer/{customerName}")
    @Operation(summary = "Get orders by customer name", description = "Retrieves all orders for a specific customer")
    @ApiResponse(responseCode = "200", description = "Orders retrieved successfully")
    public ResponseEntity<List<OrderResponse>> getOrdersByCustomer(
            @Parameter(description = "Customer name") @PathVariable String customerName) {

        log.info("Received request to get orders for customer: {}", customerName);

        List<Order> orders = orderService.getOrdersByCustomerName(customerName);
        List<OrderResponse> responses = orders.stream()
                .map(OrderResponse::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    /**
     * Finds orders by status.
     * 
     * @param status the order status
     * @return list of orders with 200 OK status
     */
    @GetMapping("/status/{status}")
    @Operation(summary = "Get orders by status", description = "Retrieves all orders with a specific status")
    @ApiResponse(responseCode = "200", description = "Orders retrieved successfully")
    public ResponseEntity<List<OrderResponse>> getOrdersByStatus(
            @Parameter(description = "Order status") @PathVariable OrderStatus status) {

        log.info("Received request to get orders with status: {}", status);

        List<Order> orders = orderService.getOrdersByStatus(status);
        List<OrderResponse> responses = orders.stream()
                .map(OrderResponse::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }
}
