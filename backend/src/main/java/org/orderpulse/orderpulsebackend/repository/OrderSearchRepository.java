package org.orderpulse.orderpulsebackend.repository;

import org.orderpulse.orderpulsebackend.document.OrderDocument;
import org.orderpulse.orderpulsebackend.entity.OrderStatus;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Elasticsearch repository for advanced order search.
 */
@Repository
public interface OrderSearchRepository extends ElasticsearchRepository<OrderDocument, Long> {

    List<OrderDocument> findByCustomerNameContainingOrProductDescriptionContaining(String customerName,
            String productDescription);

    List<OrderDocument> findByStatus(OrderStatus status);

    List<OrderDocument> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
}

