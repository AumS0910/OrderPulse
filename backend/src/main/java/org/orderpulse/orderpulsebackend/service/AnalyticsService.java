package org.orderpulse.orderpulsebackend.service;

import lombok.RequiredArgsConstructor;
import org.orderpulse.orderpulsebackend.dto.OrderAnalytics;
import org.orderpulse.orderpulsebackend.entity.OrderStatus;
import org.orderpulse.orderpulsebackend.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Computes analytics over persisted orders.
 */
@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final OrderRepository orderRepository;

    public OrderAnalytics getOrderAnalytics() {
        Long totalOrders = orderRepository.countTotalOrders();
        BigDecimal totalRevenue = orderRepository.calculateTotalRevenue();
        if (totalRevenue == null) {
            totalRevenue = BigDecimal.ZERO;
        }

        double averageOrderValue = (totalOrders != null && totalOrders > 0)
                ? totalRevenue.divide(BigDecimal.valueOf(totalOrders), 2, RoundingMode.HALF_UP).doubleValue()
                : 0.0;

        Map<String, Long> ordersByStatus = new HashMap<>();
        List<Object[]> groupedCounts = orderRepository.countOrdersByStatus();
        for (Object[] row : groupedCounts) {
            ordersByStatus.put(((OrderStatus) row[0]).name(), (Long) row[1]);
        }

        Map<String, BigDecimal> revenueByStatus = new HashMap<>();
        List<Object[]> groupedRevenue = orderRepository.calculateRevenueByStatus();
        for (Object[] row : groupedRevenue) {
            BigDecimal revenue = (BigDecimal) row[1];
            revenueByStatus.put(((OrderStatus) row[0]).name(), revenue == null ? BigDecimal.ZERO : revenue);
        }

        return OrderAnalytics.builder()
                .totalOrders(totalOrders == null ? 0L : totalOrders)
                .totalRevenue(totalRevenue)
                .ordersByStatus(ordersByStatus)
                .revenueByStatus(revenueByStatus)
                .averageOrderValue(averageOrderValue)
                .build();
    }
}

