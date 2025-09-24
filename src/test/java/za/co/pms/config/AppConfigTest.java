package za.co.pms.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author NMMkhungo
 * @since 2025/09/24
 **/
class AppConfigTest {
    @Test
    @DisplayName("")
    void testCurrencyConfigLocationBean() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class)) {
            // Ensure the bean exists
            assertTrue(context.containsBean("currencyConfigLocation"));

            // Retrieve the bean
            String location = context.getBean("currencyConfigLocation", String.class);

            // Assert that it points to the correct JSON location
            assertEquals("classpath:config/currency-config.json", location);
        }
    }
}
