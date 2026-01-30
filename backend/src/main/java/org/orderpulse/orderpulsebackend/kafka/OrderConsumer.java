package org.orderpulse.orderpulsebackend.kafka;

import org.orderpulse.orderpulsebackend.dto.OrderEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.orderpulse.orderpulsebackend.config.KafkaConfig;
import org.springframework.messaging.handler.annotation.Header;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Kafka consumer service for processing order events.
 * 
 * This service listens to the order-events topic and processes incoming events.
 * It demonstrates event-driven architecture where different parts of the system
 * can react to order changes asynchronously.
 * 
 * Use Cases:
 * - Send email notifications to customers
 * - Update inventory systems
 * - Trigger payment processing
 * - Update analytics dashboards
 * - Sync with external systems (CRM, ERP)
 * 
 * @author OrderPulse Team
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderConsumer {

    /**
     * Listens to order events from Kafka and processes them.
     * 
     * Configuration:
     * - Topic: order-events
     * - Groud ID: orderpulse-consumer-group (from application.yml)
     * - Concurrency: 3 (matches number of partitions)
     * 
     * The @KafkaListener annotation automatically:
     * - Deserializes JSON messages to OrderEvent objects
     * - Handles message delivery
     * - Provides error handling
     * - Manages consumer lifecycle
     * 
     * @param event     the order event received from Kafka
     * @param partition the partition from which the message was received
     * @param offset    the offset of the message
     */
    @KafkaListener(topics = KafkaConfig.ORDER_TOPIC, groupId = "${spring.kafka.consumer.group-id}", concurrency = "3" // Number
                                                                                                                      // of
                                                                                                                      // concurrent
                                                                                                                      // consumers
                                                                                                                      // (matches
                                                                                                                      // partitions)
    )
    public void consumeOrderEvent(
            @Payload OrderEvent event,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {

        log.info("Received order event: {} from partition: {}, offset: {}",
                event.getEventType(), partition, offset);

        try {
            // Process the event based on its type
            switch (event.getEventType()) {
                case "ORDER_CREATED":
                    handleOrderCreated(event);
                    break;

                case "ORDER_UPDATED":
                    handleOrderUpdated(event);
                    break;

                case "ORDER_CANCELLED":
                    handleOrderCancelled(event);
                    break;

                default:
                    log.warn("Unknown event type: {}", event.getEventType());
            }

            log.info("Successfully processed event: {}", event.getEventType());

        } catch (Exception e) {
            log.error("Error processing order event: {}", event.getEventType(), e);

            // In production, you might want to:
            // 1. Send to a dead letter queue (DLQ)
            // 2. Retry with exponential backoff
            // 3. Alert monitoring systems
            // 4. Store failed events for manual review

            // For now, we'll just log the error
            // The offset will still be committed, so the message won't be reprocessed
        }
    }

    /**
     * Handles ORDER_CREATED events.
     * 
     * Example actions:
     * - Send welcome email to customer
     * - Reserve inventory
     * - Create shipment record
     * - Update analytics
     * 
     * @param event the order created event
     */
    private void handleOrderCreated(OrderEvent event) {
        log.info("Processing ORDER_CREATED for order ID: {}", event.getOrder().getId());

        // Example: Send email notification
        sendOrderConfirmationEmail(event.getOrder().getCustomerEmail(), event.getOrder().getId());

        // Example: Update inventory
        // inventoryService.reserveItems(event.getOrder());

        // Example: Create analytics record
        // analyticsService.recordOrderCreated(event.getOrder());
    }

    /**
     * Handles ORDER_UPDATED events.
     * 
     * Example actions:
     * - Send status update email
     * - Update tracking information
     * - Notify customer service
     * 
     * @param event the order updated event
     */
    private void handleOrderUpdated(OrderEvent event) {
        log.info("Processing ORDER_UPDATED for order ID: {} - New status: {}", event.getOrder().getId(),
                event.getOrder().getStatus());

        // Example: Send status update email

        sendOrderStatusUpdateEmail(event.getOrder().getCustomerEmail(), event.getOrder().getId(),
                event.getOrder().getStatus().toString());

        // Example: Update external systems
        // externalSystemService.syncOrderStatus(event.getOrder());
    }

    /**
     * Handles ORDER_CANCELLED events.
     * 
     * Example actions:
     * - Send cancellation confirmation
     * - Release inventory
     * - Initiate refund process
     * 
     * @param event the order cancelled event
     */
    private void handleOrderCancelled(OrderEvent event) {
        log.info("Processing ORDER_CANCELLED for order ID: {}", event.getOrder().getId());

        // Example: Send cancellation email

        sendOrderCancellationEmail(event.getOrder().getCustomerEmail(), event.getOrder().getId());

        // Example: Release inventory
        // inventoryService.releaseItems(event.getOrder());

        // Example: Inititate refund
        // paymentService.initiateRefund(event.getOrder());
    }

    /**
     * Simulates sending order confirmation email.
     * In production, integrate with email service (SendGrid, AWS SES, etc.)
     * 
     * @param email   customer email
     * @param orderId order ID
     */
    private void sendOrderConfirmationEmail(String email, Long orderId) {
        log.info("Sending order confirmation email to {} for order {}", email, orderId);
        // Email service integration would go here
    }

    /**
     * Simulates sending order status update email.
     * 
     * @param email   customer email
     * @param orderId order ID
     * @param status  new order status
     */
    private void sendOrderStatusUpdateEmail(String email, Long orderId, String status) {
        log.info("Sending status update email to {} for order {} - Status: {}", email, orderId, status);
        // Email service integration would go here
    }

    /**
     * Simulates sending order cancellation email.
     * 
     * @param email   customer email
     * @param orderId order ID
     */
    private void sendOrderCancellationEmail(String email, Long orderId) {
        log.info("Sending cancellation email to {} for order {}", email, orderId);
        // Email service integration would go here
    }

}
