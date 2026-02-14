package org.orderpulse.orderpulsebackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.orderpulse.orderpulsebackend.document.OrderDocument;
import org.orderpulse.orderpulsebackend.entity.OrderStatus;
import org.orderpulse.orderpulsebackend.service.OrderSearchService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

/**
 * REST controller for advanced Elasticsearch order search.
 */
@RestController
@RequestMapping("/api/orders/search")
@RequiredArgsConstructor
@Tag(name = "Order Search", description = "Advanced order search using Elasticsearch")
public class OrderSearchController {

    private final OrderSearchService searchService;

    @GetMapping
    @Operation(summary = "Search orders by free text query")
    public ResponseEntity<List<OrderDocument>> searchOrders(@RequestParam String query) {
        return ResponseEntity.ok(searchService.searchOrders(query));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Search orders by status")
    public ResponseEntity<List<OrderDocument>> searchByStatus(@PathVariable OrderStatus status) {
        return ResponseEntity.ok(searchService.searchByStatus(status));
    }

    @GetMapping("/date-range")
    @Operation(summary = "Search orders by date range")
    public ResponseEntity<List<OrderDocument>> searchByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(searchService.searchByDateRange(startDate, endDate));
    }
}

