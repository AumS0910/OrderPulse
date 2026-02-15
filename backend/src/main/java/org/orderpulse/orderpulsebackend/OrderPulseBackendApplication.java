package org.orderpulse.orderpulsebackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.kafka.annotation.EnableKafka;

/**
 * Main application class for the OrderPulse Backend service.
 * 
 * @SpringBootApplication: This annotation combines:
 *                         - @Configuration: Tags the class as a source of bean
 *                         definitions
 *                         - @EnableAutoConfiguration: Tells Spring Boot to
 *                         auto-configure the application
 *                         - @ComponentScan: Tells Spring to look for other
 *                         components in the same package
 *
 */
@SpringBootApplication
@EnableKafka // Enables Kafka integration
@EnableCaching // Enables Spring's caching abstraction
public class OrderPulseBackendApplication {

    /**
     * Main method to start the application.
     * 
     * @param args command Line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(OrderPulseBackendApplication.class, args);
    }
}
