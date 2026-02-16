package org.orderpulse.orderpulsebackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.orderpulse.orderpulsebackend.dto.OrderRequest;
import org.orderpulse.orderpulsebackend.dto.OrderStatusUpdateRequest;
import org.orderpulse.orderpulsebackend.entity.Order;
import org.orderpulse.orderpulsebackend.entity.OrderStatus;
import org.orderpulse.orderpulsebackend.exception.OrderNotFoundException;
import org.orderpulse.orderpulsebackend.security.JwtAuthenticationFilter;
import org.orderpulse.orderpulsebackend.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for OrderController.
 * 
 * These tests verify the REST API endpoints using MockMvc.
 * The service layer is mocked to isolate controller logic.
 * 
 * @author OrderPulse Team
 * @version 1.0
 */
@WebMvcTest(OrderController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("OrderController Unit Tests")
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderService orderService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    private Order order;
    private OrderRequest orderRequest;

    @BeforeEach
    void setUp() {
        orderRequest = OrderRequest.builder()
                .customerName("John Doe")
                .customerEmail("john.doe@example.com")
                .productDescription("Test Product")
                .quantity(2)
                .totalPrice(new BigDecimal("99.99"))
                .build();

        order = Order.builder()
                .id(1L)
                .customerName("John Doe")
                .customerEmail("john.doe@example.com")
                .productDescription("Test Product")
                .quantity(2)
                .totalPrice(new BigDecimal("99.99"))
                .status(OrderStatus.PENDING)
                .build();
    }

    @Test
    @DisplayName("POST /api/orders - Should create order successfully")
    void testCreateOrder_Success() throws Exception {
        // Given
        when(orderService.createOrder(any(OrderRequest.class))).thenReturn(order);

        // When & Then
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.customerName").value("John Doe"))
                .andExpect(jsonPath("$.status").value("PENDING"));

        verify(orderService, times(1)).createOrder(any(OrderRequest.class));
    }

    @Test
    @DisplayName("POST /api/orders - Should return 400 for invalid input")
    void testCreateOrder_InvalidInput() throws Exception {
        // Given: Invalid order request (missing required fields)
        OrderRequest invalidRequest = OrderRequest.builder().build();

        // When & Then
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/orders/{id} - Should return order successfully")
    void testGetOrderById_Success() throws Exception {
        // Given
        when(orderService.getOrderById(1L)).thenReturn(order);

        // When & Then
        mockMvc.perform(get("/api/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.customerName").value("John Doe"));

        verify(orderService, times(1)).getOrderById(1L);
    }

    @Test
    @DisplayName("GET /api/orders/{id} - Should return 404 when order not found")
    void testGetOrderById_NotFound() throws Exception {
        // Given
        when(orderService.getOrderById(anyLong()))
                .thenThrow(new OrderNotFoundException(999L));

        // When & Then
        mockMvc.perform(get("/api/orders/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Order not found with ID: 999"));
    }

    @Test
    @DisplayName("GET /api/orders - Should return all orders")
    void testGetAllOrders_Success() throws Exception {
        // Given
        List<Order> orders = Arrays.asList(order);
        Page<Order> page = new PageImpl<>(orders, PageRequest.of(0, 20), 1);
        when(orderService.getOrders(any(), any(), any(), any(), any())).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(orderService, times(1)).getOrders(any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("PUT /api/orders/{id}/status - Should update status successfully")
    void testUpdateOrderStatus_Success() throws Exception {
        // Given
        OrderStatusUpdateRequest statusUpdate = OrderStatusUpdateRequest.builder()
                .status(OrderStatus.CONFIRMED)
                .build();

        order.setStatus(OrderStatus.CONFIRMED);
        when(orderService.updateOrderStatus(1L, OrderStatus.CONFIRMED))
                .thenReturn(order);

        // When & Then
        mockMvc.perform(put("/api/orders/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(statusUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONFIRMED"));

        verify(orderService, times(1))
                .updateOrderStatus(1L, OrderStatus.CONFIRMED);
    }

    @Test
    @DisplayName("DELETE /api/orders/{id} - Should delete order successfully")
    void testDeleteOrder_Success() throws Exception {
        // Given
        doNothing().when(orderService).deleteOrder(1L);

        // When & Then
        mockMvc.perform(delete("/api/orders/1"))
                .andExpect(status().isNoContent());

        verify(orderService, times(1)).deleteOrder(1L);
    }
}
