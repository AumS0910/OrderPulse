package org.orderpulse.orderpulsebackend.kafka;

import java.util.concurrent.CompletableFuture;

import org.orderpulse.orderpulsebackend.config.KafkaConfig;
import org.orderpulse.orderpulsebackend.dto.OrderEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Kafka producer service for publishing order events.
 * 
 * This service is responsible for sending order-related events to Kafka topics.
 * It provides asynchronous message publishing with callback handling for
 * success and failure.
 * 
 * Key Features:
 * - Asynchronous publishing for non-blocking operations
 * - Success and failure callbacks for monitoring
 * - Automatic JSON serialization of events
 * - Logging for debugging and audit trails
 * 
 * @author OrderPulse Team
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderProducer {

    /**
     * KafkaTemplate for sending messages to Kafka.
     * Configured with String key and OrderEvent value serialization.
     * Injected by Sppring's dependency injection.
     */
    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;

    /**
     * Publishes an order event to the Kafka topic.
     * 
     * The method sends the event asynchronously and registers callbacks to handle
     * the result. This ensures the calling thread is not blocked while waiting
     * for Kafka acknowledgement.
     * 
     * @param event the order event to publish
     */
    public void publishOrderEvent(OrderEvent event) {
        log.info("Publishing order event: {} for order ID: {}", event.getEventType(), event.getOrder().getId());

        // Send message asynchronously to Kafka topic
        // Key: order ID (ensures all events for same order go to same)
        // Value: the complete order event
        CompletableFuture<SendResult<String, OrderEvent>> future = kafkaTemplate.send(KafkaConfig.ORDER_TOPIC,
                String.valueOf(event.getOrder().getId()), event);

        // Register success callback
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                // Message sent successfully
                log.info("Order event published successfully: {} - Partition: {}, offset: {}", event.getEventType(),
                        result.getRecordMetadata().partition(), result.getRecordMetadata().offset());

            } else {
                // Message sending failed
                log.error("Failed to publish order event: {} for order ID: {}. Error: {}", event.getEventType(),
                        event.getOrder().getId(), ex.getMessage(), ex);

                // Inproduction, you might want to:
                // 1. Retry sending the message
                // 2. Send to a dead letter queue
                // 3. Alert monitoring systems
                // 4. Store in a database for manual reprocessing
            }

        });

    }

    /**
     * Synchronous version of publishOrderEvent for cases where you need to wait
     * for confirmation before proceeding.
     * 
     * Use sparingly as it blocks the calling thread.
     * 
     * @param event the order event to publish
     * @throws Exception if publishing fails
     */
    public void publishOrderEventSync(OrderEvent event) throws Exception {
        log.info("Publishing order event synchronously: {}", event.getEventType());

        SendResult<String, OrderEvent> result = kafkaTemplate
                .send(KafkaConfig.ORDER_TOPIC, String.valueOf(event.getOrder().getId()), event).get();

        log.info("Order event published: Partition: {}, offset: {}", result.getRecordMetadata().partition(),
                result.getRecordMetadata().offset());
    }

}
