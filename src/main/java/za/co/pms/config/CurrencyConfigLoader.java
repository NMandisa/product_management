package za.co.pms.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import za.co.pms.exception.CurrencyConfigException;
import za.co.pms.exception.CurrencyConfigNotFoundException;
import za.co.pms.exception.CurrencyConfigParseException;
import za.co.pms.exception.CurrencyConfigValidationException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author NMMkhungo
 * @since 2025/09/23
 **/
@Component
@Slf4j
public class CurrencyConfigLoader implements ResourceLoaderAware {

    private final ObjectMapper objectMapper;
    private final Validator validator;
    private ResourceLoader resourceLoader;
    private final String resourceLocation; // <-- must declare
    @Getter
    private CurrencyConfig currencyConfig;

    @Autowired
    public CurrencyConfigLoader(ObjectMapper objectMapper,
                                Validator validator,
                                String currencyConfigLocation) { // injected bean
        this.objectMapper = objectMapper;
        this.validator = validator;
        this.resourceLocation = currencyConfigLocation; // <-- store it!
    }

    @Autowired
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    /**
     * Load and validate config at startup.
     */
    @PostConstruct
    public void loadCurrencyConfig() {
        this.currencyConfig = loadCurrencyConfigAndReturn();
    }

    /**
     * Load and return CurrencyConfig (with validation).
     */
    public CurrencyConfig loadCurrencyConfigAndReturn() {
        try {
            Resource resource = resourceLoader.getResource(resourceLocation);
            if (!resource.exists()) {
                throw new CurrencyConfigNotFoundException(
                        "Currency config file not found at: " + resourceLocation
                );
            }

            try (InputStream is = resource.getInputStream()) {
                CurrencyConfig config = objectMapper.readValue(is, CurrencyConfig.class);

                Set<ConstraintViolation<CurrencyConfig>> violations = validator.validate(config);
                if (!violations.isEmpty()) {
                    String message = violations.stream()
                            .map(ConstraintViolation::getMessage)
                            .collect(Collectors.joining(", "));
                    throw new CurrencyConfigValidationException(
                            "Currency config validation failed: " + message, violations
                    );
                }

                return config;
            }
        } catch (JsonProcessingException ex) {
            throw new CurrencyConfigParseException("Failed to parse currency config", ex);
        } catch (IOException ex) {
            throw new CurrencyConfigException("Unexpected IO error during config loading", ex);
        }
    }
}
