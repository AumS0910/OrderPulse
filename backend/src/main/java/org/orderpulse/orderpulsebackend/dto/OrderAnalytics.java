package org.orderpulse.orderpulsebackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Aggregate analytics payload for order dashboards.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderAnalytics {

    private Long totalOrders;
    private BigDecimal totalRevenue;
    private Map<String, Long> ordersByStatus;
    private Map<String, BigDecimal> revenueByStatus;
    private Double averageOrderValue;
}

