package org.orderpulse.orderpulsebackend.dto;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.orderpulse.orderpulsebackend.entity.OrderStatus;
/**
 * DTO for updating order status.
 * 
 * @author OrderPulse Team
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderStatusUpdateRequest {
    
    /**
     * New status for the order.
     * Required field.
     */
    @NotNull(message = "Status is required")
    private OrderStatus status;
}