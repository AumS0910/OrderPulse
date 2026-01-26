package org.orderpulse.orderpulsebackend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity class representing an Order in the system.
 * 
 * This class maps to the 'orders' table in the database and contains all
 * order-related information.
 * It includes audit fields for tracking creation and modification times, and
 * uses optimistic locking
 * to handle concurrent modifications.
 * 
 * Key Features:
 * - JPA entity with automatic auditing
 * - Bean validation for data integrity
 * - Optimistic locking for concurrency control
 * - Lombok annotations to reduce boilerplate
 * 
 * @author OrderPulse Team
 * @version 1.0
 */
@Entity
@Table(name = "orders", indexes = {
        @Index(name = "idx_customer_name", columnList = "customer_name"),
        @Index(name = "idx_status", columnList = "status"),
        @Index(name = "idx_created_at", columnList = "created_at")
})
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Order {

    /**
     * Unique identifier for the order.
     * Auto-generated using database sequence.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Name of the customer placing the order.
     * Required field with length constraints.
     */
    @NotBlank(message = "Customer name is required")
    @Size(min = 2, max = 100, message = "Customer name must be between 2 and 100 characters")
    @Column(name = "customer_name", nullable = false, length = 100)
    private String customerName;

    /**
     * Email address of the customer.
     * Must be a valid email format.
     */
    @NotBlank(message = "Customer email is required")
    @Email(message = "Invalid email format")
    @Column(name = "customer_email", nullable = false, length = 150)
    private String customerEmail;

    /**
     * Description of the product or service being ordered.
     * Required field with maximum length.
     */
    @NotBlank(message = "Product description is required")
    @Size(max = 500, message = "Product description cannot exceed 500 characters")
    @Column(name = "product_description", nullable = false, length = 500)
    private String productDescription;

    /**
     * Quantity of items ordered.
     * Must be a positive integer.
     */
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    @Max(value = 10000, message = "Quantity cannot exceed 10000")
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    /**
     * Total price of the order.
     * Uses BigDecimal for precise monetary calculations.
     * Precision: 10 digits total, 2 decimal places.
     */
    @NotNull(message = "Total price is required")
    @DecimalMin(value = "0.01", message = "Total price must be greater than 0")
    @DecimalMax(value = "99999999.99", message = "Total price is too large")
    @Column(name = "total_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice;

    /**
     * Current status of the order.
     * Stored as string in database, mapped to OrderStatus enum.
     */
    @NotNull(message = "Order status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private OrderStatus status;

    /**
     * Timestamp when the order was created.
     * Automatically populated by JPA auditing.
     */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when the order was last modified.
     * Automatically updated by JPA auditing on every change.
     */
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Version field for optimistic locking.
     * Prevents lost updates when multiple users modify the same order concurrently.
     * Automatically incremented by JPA on each update.
     */
    @Version
    @Column(name = "version")
    private Long version;

    /**
     * Pre-persist callback to set default values before saving a new order.
     * Sets status to PENDING if not already set.
     */
    @PrePersist
    protected void onCreate() {
        if (status == null) {
            status = OrderStatus.PENDING;
        }
    }
}