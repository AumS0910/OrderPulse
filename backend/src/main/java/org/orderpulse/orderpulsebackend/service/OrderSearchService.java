package org.orderpulse.orderpulsebackend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.orderpulse.orderpulsebackend.document.OrderDocument;
import org.orderpulse.orderpulsebackend.entity.Order;
import org.orderpulse.orderpulsebackend.entity.OrderStatus;
import org.orderpulse.orderpulsebackend.repository.OrderSearchRepository;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * Service for indexing and searching orders in Elasticsearch.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderSearchService {

    private final ObjectProvider<OrderSearchRepository> searchRepositoryProvider;

    @Value("${app.search.elasticsearch-enabled:true}")
    private boolean elasticsearchEnabled;

    public void indexOrder(Order order) {
        if (!elasticsearchEnabled) {
            return;
        }

        OrderSearchRepository searchRepository = searchRepositoryProvider.getIfAvailable();
        if (searchRepository == null) {
            log.debug("Elasticsearch repository unavailable; skipping index for order {}", order.getId());
            return;
        }

        try {
            OrderDocument document = OrderDocument.builder()
                    .id(order.getId())
                    .customerName(order.getCustomerName())
                    .customerEmail(order.getCustomerEmail())
                    .productDescription(order.getProductDescription())
                    .productSku(order.getProductSku())
                    .quantity(order.getQuantity())
                    .totalPrice(order.getTotalPrice())
                    .status(order.getStatus())
                    .createdAt(order.getCreatedAt())
                    .build();

            searchRepository.save(document);
            log.debug("Indexed order {} in Elasticsearch", order.getId());
        } catch (Exception ex) {
            log.warn("Failed to index order {} in Elasticsearch: {}", order.getId(), ex.getMessage());
        }
    }

    public List<OrderDocument> searchOrders(String query) {
        if (!elasticsearchEnabled) {
            return Collections.emptyList();
        }
        OrderSearchRepository searchRepository = searchRepositoryProvider.getIfAvailable();
        if (searchRepository == null) {
            return Collections.emptyList();
        }
        try {
            return searchRepository.findByCustomerNameContainingOrProductDescriptionContaining(query, query);
        } catch (Exception ex) {
            log.warn("Elasticsearch query failed: {}", ex.getMessage());
            return Collections.emptyList();
        }
    }

    public List<OrderDocument> searchByStatus(OrderStatus status) {
        if (!elasticsearchEnabled) {
            return Collections.emptyList();
        }
        OrderSearchRepository searchRepository = searchRepositoryProvider.getIfAvailable();
        if (searchRepository == null) {
            return Collections.emptyList();
        }
        try {
            return searchRepository.findByStatus(status);
        } catch (Exception ex) {
            log.warn("Elasticsearch status query failed: {}", ex.getMessage());
            return Collections.emptyList();
        }
    }

    public List<OrderDocument> searchByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        if (!elasticsearchEnabled) {
            return Collections.emptyList();
        }
        OrderSearchRepository searchRepository = searchRepositoryProvider.getIfAvailable();
        if (searchRepository == null) {
            return Collections.emptyList();
        }
        try {
            return searchRepository.findByCreatedAtBetween(startDate, endDate);
        } catch (Exception ex) {
            log.warn("Elasticsearch date range query failed: {}", ex.getMessage());
            return Collections.emptyList();
        }
    }

    public void deleteOrder(Long orderId) {
        if (!elasticsearchEnabled) {
            return;
        }
        OrderSearchRepository searchRepository = searchRepositoryProvider.getIfAvailable();
        if (searchRepository == null) {
            return;
        }
        try {
            searchRepository.deleteById(orderId);
            log.debug("Deleted order {} from Elasticsearch", orderId);
        } catch (Exception ex) {
            log.warn("Failed to delete indexed order {}: {}", orderId, ex.getMessage());
        }
    }
}
