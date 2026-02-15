package org.orderpulse.orderpulsebackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.orderpulse.orderpulsebackend.dto.OrderAnalytics;
import org.orderpulse.orderpulsebackend.service.AnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller exposing analytics endpoints.
 */
@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
@Tag(name = "Analytics", description = "Order analytics and reporting")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/orders")
    @Operation(summary = "Get order analytics")
    public ResponseEntity<OrderAnalytics> getOrderAnalytics() {
        return ResponseEntity.ok(analyticsService.getOrderAnalytics());
    }
}

