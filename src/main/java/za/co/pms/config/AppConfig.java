package za.co.pms.config;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @author NMMkhungo
 * @since 2025/09/23
 **/
@EnableAspectJAutoProxy
@Configuration
public class AppConfig {
    @Bean
    public String currencyConfigLocation() {
        return "classpath:config/currency-config.json";
    }
    /**
     * Provides a simple MeterRegistry for recording metrics.
     * In production, you can replace this with PrometheusMeterRegistry or another implementation.
     */
    @Bean
    public MeterRegistry meterRegistry() {
        return new SimpleMeterRegistry(); // Simple in-memory registry
    }
}