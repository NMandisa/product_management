package za.co.pms.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import za.co.pms.exception.CurrencyConfigNotFoundException;
import za.co.pms.exception.CurrencyConfigParseException;
import za.co.pms.exception.CurrencyConfigValidationException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
/**
 * @author NMMkhungo
 * @since 2025/09/23
 **/

@ExtendWith(MockitoExtension.class)
class CurrencyConfigLoaderTest {

    @Mock
    private ResourceLoader resourceLoader;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private Validator validator;
    @Mock
    private Resource resource;

    private CurrencyConfigLoader loader;

    @BeforeEach
    void setUp() {
        loader = new CurrencyConfigLoader(
                objectMapper,
                validator,
                "classpath:config/currency-config.json"
        );
        loader.setResourceLoader(resourceLoader);
    }

    @Test
    @DisplayName("Should load valid currency config successfully")
    void loadCurrencyConfig_shouldLoadValidConfig_whenFileExists() throws Exception {
        // Arrange
        InputStream json = new ByteArrayInputStream("{\"default\":\"USD\",\"supported\":[]}".getBytes());
        CurrencyConfig config = new CurrencyConfig();
        config.setDefaultCurrency("USD");
        config.setSupported(new ArrayList<>());

        when(resourceLoader.getResource(anyString())).thenReturn(resource);
        when(resource.exists()).thenReturn(true);
        when(resource.getInputStream()).thenReturn(json);
        when(objectMapper.readValue(any(InputStream.class), eq(CurrencyConfig.class))).thenReturn(config);
        when(validator.validate(config)).thenReturn(Collections.emptySet());

        // Act
        loader.loadCurrencyConfig();

        // Assert
        assertThat(loader.getCurrencyConfig()).isNotNull();
        assertThat(loader.getCurrencyConfig().getDefaultCurrency()).isEqualTo("USD");
    }

    @Test
    @DisplayName("Should throw CurrencyConfigNotFoundException if file is missing")
    void loadCurrencyConfig_shouldThrowNotFoundException_whenFileMissing() {
        when(resourceLoader.getResource(anyString())).thenReturn(resource);
        when(resource.exists()).thenReturn(false);

        assertThatThrownBy(() -> loader.loadCurrencyConfig())
                .isInstanceOf(CurrencyConfigNotFoundException.class)
                .hasMessageContaining("currency-config.json");
    }

    @Test
    @DisplayName("Should throw CurrencyConfigParseException if JSON is invalid")
    void loadCurrencyConfig_shouldThrowParseException_whenJsonIsInvalid() throws Exception {
        when(resourceLoader.getResource(anyString())).thenReturn(resource);
        when(resource.exists()).thenReturn(true);
        when(resource.getInputStream()).thenReturn(new ByteArrayInputStream("{}".getBytes()));
        when(objectMapper.readValue(any(InputStream.class), eq(CurrencyConfig.class)))
                .thenThrow(new CurrencyConfigParseException("Bad JSON"));

        assertThatThrownBy(() -> loader.loadCurrencyConfig())
                .isInstanceOf(CurrencyConfigParseException.class)
                .hasMessageContaining("Bad JSON");
    }

    @Test
    @DisplayName("Should throw CurrencyConfigValidationException when validation fails")
    void loadCurrencyConfig_shouldThrowValidationException_whenConstraintsFail() throws Exception {
        InputStream json = new ByteArrayInputStream("{\"default\":\"USD\",\"supported\":[]}".getBytes());
        CurrencyConfig config = new CurrencyConfig();
        config.setDefaultCurrency("USD");
        config.setSupported(new ArrayList<>());

        ConstraintViolation<CurrencyConfig> violation = mock(ConstraintViolation.class);
        when(violation.getMessage()).thenReturn("must not be blank");

        when(resourceLoader.getResource(anyString())).thenReturn(resource);
        when(resource.exists()).thenReturn(true);
        when(resource.getInputStream()).thenReturn(json);
        when(objectMapper.readValue(any(InputStream.class), eq(CurrencyConfig.class))).thenReturn(config);
        when(validator.validate(config)).thenReturn(Set.of(violation));

        assertThatThrownBy(() -> loader.loadCurrencyConfig())
                .isInstanceOf(CurrencyConfigValidationException.class)
                .hasMessageContaining("must not be blank");
    }

    // Example of parameterized test using CSV for invalid JSON inputs
    @ParameterizedTest(name = "{index} => invalidJson=''{0}'' should throw parse exception")
    @CsvSource({
            "'{'",  // incomplete JSON
            "'{\"default\":123}'", // wrong type
            "'invalid json'"        // non-JSON text
    })
    @DisplayName("Parameterized: Invalid JSON inputs should throw parse exception")
    void loadCurrencyConfig_shouldThrowParseException_forInvalidJson(String invalidJson) throws Exception {
        when(resourceLoader.getResource(anyString())).thenReturn(resource);
        when(resource.exists()).thenReturn(true);
        when(resource.getInputStream()).thenReturn(new ByteArrayInputStream(invalidJson.getBytes()));
        when(objectMapper.readValue(any(InputStream.class), eq(CurrencyConfig.class)))
                .thenThrow(new CurrencyConfigParseException("Bad JSON"));

        assertThatThrownBy(() -> loader.loadCurrencyConfig())
                .isInstanceOf(CurrencyConfigParseException.class)
                .hasMessageContaining("Bad JSON");
    }
}
