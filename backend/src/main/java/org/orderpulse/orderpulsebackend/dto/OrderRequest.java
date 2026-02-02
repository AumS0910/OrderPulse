package org.orderpulse.orderpulsebackend.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

/**
 * Data Transfer Object for creating or updating orders.
 * 
 * This DTO is used to receive order data from API clients.
 * It includes validation annotations to ensure data integrity before
 * processing.
 * 
 * @author OrderPulse Team
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequest {

    /**
     * Customer name.
     * Required, must be between 2 and 100 characters.
     */
    @NotBlank(message = "Customer name is required")
    @Size(min = 2, max = 100, message = "Customer name must be between 2 and 100 characters")
    private String customerName;

    /**
     * Customer email address.
     * Required, must be valid email format.
     */
    @NotBlank(message = "Customer email is required")
    @Email(message = "Please provide a valid email address")
    private String customerEmail;

    /**
     * Product description.
     * Required, maximum 500 characters.
     */
    @NotBlank(message = "Product description is required")
    @Size(max = 500, message = "Product description cannot exceed 500 characters")
    private String productDescription;

    /**
     * Quantity of items.
     * Required, must be between 1 and 10000.
     */
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    @Max(value = 10000, message = "Quantity cannot exceed 10000")
    private Integer quantity;

    /**
     * Total price of the order.
     * Required, must be positive.
     */
    @NotNull(message = "Total price is required")
    @DecimalMin(value = "0.01", message = "Total price must be greater than 0")
    @DecimalMax(value = "99999999.99", message = "Total price exceeds maximum allowed")
    private BigDecimal totalPrice;
}