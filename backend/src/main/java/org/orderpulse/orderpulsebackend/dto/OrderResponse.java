package org.orderpulse.orderpulsebackend.dto;

import lombok.*;
import org.orderpulse.orderpulsebackend.entity.Order;
import org.orderpulse.orderpulsebackend.entity.OrderStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for order responses.
 * 
 * This DTO is used to send order data to API clients.
 * It excludes sensitive internal fields like version.
 * 
 * @author OrderPulse Team
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {

    private Long id;
    private String customerName;
    private String customerEmail;
    private String productDescription;
    private Integer quantity;
    private BigDecimal totalPrice;
    private OrderStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Factory method to convert Order entity to OrderResponse DTO.
     * 
     * @param order the order entity
     * @return OrderResponse DTO
     */
    public static OrderResponse fromEntity(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .customerName(order.getCustomerName())
                .customerEmail(order.getCustomerEmail())
                .productDescription(order.getProductDescription())
                .quantity(order.getQuantity())
                .totalPrice(order.getTotalPrice())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }
}