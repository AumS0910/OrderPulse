package org.orderpulse.orderpulsebackend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * JPA configuration to enable automatic auditing.
 * 
 * This enables automatic population of @CreatedDate and @LastModifiedDate
 * fields.
 * 
 * @author OrderPulse Team
 * @version 1.0
 */
@Configuration
@EnableJpaAuditing
public class JpaConfig {
    // Configuration class to enable JPA auditing
}