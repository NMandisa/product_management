package za.co.pms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author NMMkhungo
 * @since 2025/09/23
 **/
@Configuration
public class AppConfig {
    @Bean
    public String currencyConfigLocation() {
        return "classpath:config/currency-config.json";
    }
}