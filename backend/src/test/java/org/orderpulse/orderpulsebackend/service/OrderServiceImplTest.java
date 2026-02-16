package org.orderpulse.orderpulsebackend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.orderpulse.orderpulsebackend.dto.OrderRequest;
import org.orderpulse.orderpulsebackend.entity.Order;
import org.orderpulse.orderpulsebackend.entity.OrderStatus;
import org.orderpulse.orderpulsebackend.exception.OrderNotFoundException;
import org.orderpulse.orderpulsebackend.kafka.OrderProducer;
import org.orderpulse.orderpulsebackend.repository.OrderRepository;
import org.orderpulse.orderpulsebackend.service.OrderSearchService;
import org.orderpulse.orderpulsebackend.service.InventoryService;
import org.orderpulse.orderpulsebackend.service.impl.OrderServiceImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Unit tests for OrderServiceImpl.
 * 
 * These tests verify the business logic in the service layer using mocks
 * for dependencies (repository and Kafka producer).
 * 
 * Testing Strategy:
 * - Use Mockito for mocking dependencies
 * - Test happy paths and error scenarios
 * - Verify method calls and interactions
 * - Use AssertJ for fluent assertions
 * 
 * @author OrderPulse Team
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("OrderService Unit Tests")
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderProducer orderProducer;

    @Mock
    private OrderSearchService orderSearchService;

    @Mock
    private InventoryService inventoryService;

    @InjectMocks
    private OrderServiceImpl orderService;

    private OrderRequest orderRequest;
    private Order order;

    /**
     * Set up test data before each test.
     */
    @BeforeEach
    void setUp() {
        // Create sample order request
        orderRequest = OrderRequest.builder()
                .customerName("John Doe")
                .customerEmail("john.doe@example.com")
                .productDescription("Test Product")
                .quantity(2)
                .totalPrice(new BigDecimal("99.99"))
                .build();

        // Create sample order entity
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
    @DisplayName("Should create order successfully")
    void testCreateOrder_Success() {
        // Given: Repository will return saved order
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // When: Creating an order
        Order createdOrder = orderService.createOrder(orderRequest);

        // Then: Verify the order was created correctly
        assertThat(createdOrder).isNotNull();
        assertThat(createdOrder.getId()).isEqualTo(1L);
        assertThat(createdOrder.getCustomerName()).isEqualTo("John Doe");
        assertThat(createdOrder.getStatus()).isEqualTo(OrderStatus.PENDING);

        // Verify repository save was called
        verify(orderRepository, times(1)).save(any(Order.class));

        // Verify Kafka event was published
        verify(orderProducer, times(1)).publishOrderEvent(any());
    }

    @Test
    @DisplayName("Should get order by ID successfully")
    void testGetOrderById_Success() {
        // Given: Repository will return the order
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        // When: Getting order by ID
        Order foundOrder = orderService.getOrderById(1L);

        // Then: Verify the order was found
        assertThat(foundOrder).isNotNull();
        assertThat(foundOrder.getId()).isEqualTo(1L);
        assertThat(foundOrder.getCustomerName()).isEqualTo("John Doe");

        // Verify repository method was called
        verify(orderRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when order not found")
    void testGetOrderById_NotFound() {
        // Given: Repository will return empty
        when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then: Verify exception is thrown
        assertThatThrownBy(() -> orderService.getOrderById(999L))
                .isInstanceOf(OrderNotFoundException.class)
                .hasMessageContaining("Order not found with ID: 999");

        // Verify repository method was called
        verify(orderRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Should get all orders successfully")
    void testGetAllOrders_Success() {
        // Given: Repository will return list of orders
        Order order2 = Order.builder()
                .id(2L)
                .customerName("Jane Smith")
                .customerEmail("jane@example.com")
                .productDescription("Another Product")
                .quantity(1)
                .totalPrice(new BigDecimal("49.99"))
                .status(OrderStatus.CONFIRMED)
                .build();

        List<Order> orders = Arrays.asList(order, order2);
        when(orderRepository.findAll()).thenReturn(orders);

        // When: Getting all orders
        List<Order> foundOrders = orderService.getAllOrders();

        // Then: Verify all orders were returned
        assertThat(foundOrders).hasSize(2);
        assertThat(foundOrders).contains(order, order2);

        // Verify repository method was called
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should update order status successfully")
    void testUpdateOrderStatus_Success() {
        // Given: Repository will return the order and save it
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // When: Updating order status
        Order updatedOrder = orderService.updateOrderStatus(1L, OrderStatus.CONFIRMED);

        // Then: Verify the status was updated
        assertThat(updatedOrder).isNotNull();
        assertThat(updatedOrder.getStatus()).isEqualTo(OrderStatus.CONFIRMED);

        // Verify repository methods were called
        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).save(any(Order.class));

        // Verify Kafka event was published
        verify(orderProducer, times(1)).publishOrderEvent(any());
    }

    @Test
    @DisplayName("Should delete order successfully")
    void testDeleteOrder_Success() {
        // Given: Repository will return the order
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        doNothing().when(orderRepository).delete(any(Order.class));

        // When: Deleting the order
        orderService.deleteOrder(1L);

        // Then: Verify repository methods were called
        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).delete(order);
    }

    @Test
    @DisplayName("Should get orders by customer name")
    void testGetOrdersByCustomerName_Success() {
        // Given: Repository will return orders for customer
        List<Order> orders = Arrays.asList(order);
        when(orderRepository.findByCustomerNameIgnoreCase("John Doe"))
                .thenReturn(orders);

        // When: Getting orders by customer name
        List<Order> foundOrders = orderService.getOrdersByCustomerName("John Doe");

        // Then: Verify orders were found
        assertThat(foundOrders).hasSize(1);
        assertThat(foundOrders.get(0).getCustomerName()).isEqualTo("John Doe");

        // Verify repository method was called
        verify(orderRepository, times(1))
                .findByCustomerNameIgnoreCase("John Doe");
    }

    @Test
    @DisplayName("Should get orders by status")
    void testGetOrdersByStatus_Success() {
        // Given: Repository will return orders with status
        List<Order> orders = Arrays.asList(order);
        when(orderRepository.findByStatus(OrderStatus.PENDING))
                .thenReturn(orders);

        // When: Getting orders by status
        List<Order> foundOrders = orderService.getOrdersByStatus(OrderStatus.PENDING);

        // Then: Verify orders were found
        assertThat(foundOrders).hasSize(1);
        assertThat(foundOrders.get(0).getStatus()).isEqualTo(OrderStatus.PENDING);

        // Verify repository method was called
        verify(orderRepository, times(1)).findByStatus(OrderStatus.PENDING);
    }

    @Test
    @DisplayName("Should get paginated orders with filters")
    void testGetOrders_WithFilters_Success() {
        // Given
        PageRequest pageable = PageRequest.of(0, 10);
        Page<Order> page = new PageImpl<>(List.of(order), pageable, 1);

        when(orderRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

        // When
        Page<Order> foundPage = orderService.getOrders(
                OrderStatus.PENDING,
                "John",
                LocalDateTime.now().minusDays(7),
                LocalDateTime.now(),
                pageable);

        // Then
        assertThat(foundPage).isNotNull();
        assertThat(foundPage.getTotalElements()).isEqualTo(1);
        assertThat(foundPage.getContent()).hasSize(1);
        verify(orderRepository, times(1)).findAll(any(Specification.class), eq(pageable));
    }
}
