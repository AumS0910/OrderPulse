package org.orderpulse.orderpulsebackend.config;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
/**
 * Kafka configuration class for topic creation and management.
 * 
 * This configuration ensures that required Kafka topics exist before the application starts.
 * Topics are created with specific partitions and replication factors for optimal performance.
 * 
 * @author OrderPulse Team
 * @version 1.0
 */
@Configuration
public class KafkaConfig {
    
    /**
     * Topic name constant for order events.
     * Centralized definition prevents typos and ensures consistency.
     */
    public static final String ORDER_TOPIC = "order-events";
    
    /**
     * Creates the order-events topic if it doesn't exist.
     * 
     * Configuration:
     * - 3 partitions: Allows 3 concurrent consumers for parallel processing
     * - Replication factor 1: For development (use 3 in production for fault tolerance)
     * 
     * @return NewTopic configuration for order events
     */
    @Bean
    public NewTopic orderTopic() {
        return TopicBuilder.name(ORDER_TOPIC)
                .partitions(3)  // Number of partitions for parallel processing
                .replicas(1)    // Replication factor (increase in production)
                .build();
    }
    
    /**
     * Additional topic for dead letter queue (DLQ).
     * Failed messages are sent here for manual review and reprocessing.
     * 
     * @return NewTopic configuration for DLQ
     */
    @Bean
    public NewTopic orderDlqTopic() {
        return TopicBuilder.name(ORDER_TOPIC + ".dlq")
                .partitions(1)
                .replicas(1)
                .build();
    }
}