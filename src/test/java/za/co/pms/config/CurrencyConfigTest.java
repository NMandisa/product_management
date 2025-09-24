package za.co.pms.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author NMMkhungo
 * @since 2025/09/24
 **/
@DisplayName("CurrencyConfig Unit Tests")
class CurrencyConfigTest {
    private CurrencyConfig currencyConfig;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        currencyConfig = new CurrencyConfig();
        objectMapper = new ObjectMapper();
    }

    @Nested
    @DisplayName("Currency Configuration Core Properties")
    class CorePropertiesTests {

        @Test
        @DisplayName("Should set and get default currency correctly")
        void setDefaultCurrency_shouldSetValue_whenValidCurrencyProvided() {
            // Arrange
            String expectedCurrency = "USD";

            // Act
            currencyConfig.setDefaultCurrency(expectedCurrency);

            // Assert
            assertThat(currencyConfig.getDefaultCurrency()).isEqualTo(expectedCurrency);
        }

        @Test
        @DisplayName("Should set and get FX provider configuration")
        void setFxProvider_shouldSetValue_whenValidProviderProvided() {
            // Arrange
            String expectedProvider = "openexchangerates.org";

            // Act
            currencyConfig.setFxProvider(expectedProvider);

            // Assert
            assertThat(currencyConfig.getFxProvider()).isEqualTo(expectedProvider);
        }

        @Test
        @DisplayName("Should set and get markup percentage")
        void setMarkup_shouldSetValue_whenValidPercentageProvided() {
            // Arrange
            double expectedMarkup = 0.05;

            // Act
            currencyConfig.setMarkup(expectedMarkup);

            // Assert
            assertThat(currencyConfig.getMarkup()).isEqualTo(expectedMarkup);
        }

        @ParameterizedTest
        @ValueSource(booleans = {true, false})
        @DisplayName("Should set and get auto-detect feature flag")
        void setAutoDetect_shouldSetValue_whenBooleanProvided(boolean autoDetectFlag) {
            // Act
            currencyConfig.setAutoDetect(autoDetectFlag);

            // Assert
            assertThat(currencyConfig.isAutoDetect()).isEqualTo(autoDetectFlag);
        }
    }

    @Nested
    @DisplayName("Supported Currencies Management")
    class SupportedCurrenciesTests {

        @Test
        @DisplayName("Should add and retrieve supported currencies")
        void setSupportedCurrencies_shouldStoreCurrencies_whenValidListProvided() {
            // Arrange
            CurrencyConfig.Currency usd = createCurrency("USD", "$", "US Dollar", 2);
            CurrencyConfig.Currency eur = createCurrency("EUR", "€", "Euro", 2);
            List<CurrencyConfig.Currency> expectedCurrencies = Arrays.asList(usd, eur);

            // Act
            currencyConfig.setSupported(expectedCurrencies);

            // Assert
            assertThat(currencyConfig.getSupported())
                    .hasSize(2)
                    .extracting(CurrencyConfig.Currency::getCode)
                    .containsExactly("USD", "EUR");
        }

        @Test
        @DisplayName("Should handle empty supported currencies list")
        void setSupportedCurrencies_shouldHandleEmptyList_whenNoCurrenciesProvided() {
            // Act
            currencyConfig.setSupported(Arrays.asList());

            // Assert
            assertThat(currencyConfig.getSupported()).isEmpty();
        }

        @Test
        @DisplayName("Should handle null supported currencies list")
        void setSupportedCurrencies_shouldHandleNull_whenNullProvided() {
            // Act
            currencyConfig.setSupported(null);

            // Assert
            assertThat(currencyConfig.getSupported()).isNull();
        }
    }

    @Nested
    @DisplayName("Currency Formatting Configuration")
    class FormattingTests {

        @Test
        @DisplayName("Should set and get currency formatting rules")
        void setFormattingRules_shouldStoreRules_whenValidFormattingProvided() {
            // Arrange
            CurrencyConfig.Formatting usdFormatting = createFormatting(",", ".", "before", false);
            CurrencyConfig.Formatting eurFormatting = createFormatting(".", ",", "after", true);

            Map<String, CurrencyConfig.Formatting> formattingMap = new HashMap<>();
            formattingMap.put("USD", usdFormatting);
            formattingMap.put("EUR", eurFormatting);

            // Act
            currencyConfig.setFormatting(formattingMap);

            // Assert
            assertThat(currencyConfig.getFormatting())
                    .hasSize(2)
                    .containsKeys("USD", "EUR");

            assertThat(currencyConfig.getFormatting().get("USD"))
                    .returns(",", CurrencyConfig.Formatting::getThousandsSeparator)
                    .returns(".", CurrencyConfig.Formatting::getDecimalSeparator)
                    .returns("before", CurrencyConfig.Formatting::getSymbolPosition)
                    .returns(false, CurrencyConfig.Formatting::isSpaceBetweenSymbol);
        }

        @ParameterizedTest
        // AFTER (fixed):
        @CsvSource({
                "',', '.', 'before', false",
                "'.', ',', 'after', true",
                "' ', '.', 'before', true"
        })
        @DisplayName("Should validate formatting rules for different currency patterns")
        void formattingRules_shouldBeConsistent_whenVariousPatternsProvided(
                String thousandsSep, String decimalSep, String symbolPos, boolean spaceBetween) {
            // Arrange
            CurrencyConfig.Formatting formatting = createFormatting(thousandsSep, decimalSep, symbolPos, spaceBetween);

            // Assert
            assertThat(formatting)
                    .returns(thousandsSep, CurrencyConfig.Formatting::getThousandsSeparator)
                    .returns(decimalSep, CurrencyConfig.Formatting::getDecimalSeparator)
                    .returns(symbolPos, CurrencyConfig.Formatting::getSymbolPosition)
                    .returns(spaceBetween, CurrencyConfig.Formatting::isSpaceBetweenSymbol);
        }
    }

    @Nested
    @DisplayName("Regional Defaults Configuration")
    class RegionalDefaultsTests {

        @Test
        @DisplayName("Should set and get regional currency defaults")
        void setRegionalDefaults_shouldStoreMappings_whenValidCountryCurrencyPairsProvided() {
            // Arrange
            Map<String, String> regionalDefaults = Map.of(
                    "US", "USD",
                    "ZA", "ZAR",
                    "NG", "NGN",
                    "KE", "KES"
            );

            // Act
            currencyConfig.setRegionalDefaults(regionalDefaults);

            // Assert
            assertThat(currencyConfig.getRegionalDefaults())
                    .hasSize(4)
                    .containsEntry("US", "USD")
                    .containsEntry("ZA", "ZAR");
        }

        @Test
        @DisplayName("Should return null when regional defaults not set")
        void getRegionalDefaults_shouldReturnNull_whenDefaultsNotConfigured() {
            // Assert
            assertThat(currencyConfig.getRegionalDefaults()).isNull();
        }
    }

    @Nested
    @DisplayName("JSON Serialization and Deserialization")
    class JsonSerializationTests {

        @Test
        @DisplayName("Should serialize currency config to JSON successfully")
        void serializeToJson_shouldProduceValidJson_whenValidConfigProvided() throws Exception {
            // Arrange
            currencyConfig.setDefaultCurrency("USD");
            currencyConfig.setSupported(Arrays.asList(createCurrency("USD", "$", "US Dollar", 2)));
            currencyConfig.setAutoDetect(true);
            currencyConfig.setMarkup(0.05);

            // Act
            String json = objectMapper.writeValueAsString(currencyConfig);

            // Assert
            assertThat(json)
                    .isNotBlank()
                    .contains("\"defaultCurrency\":\"USD\"")
                    .contains("\"autoDetect\":true")
                    .contains("\"markup\":0.05");
        }

        @Test
        @DisplayName("Should deserialize JSON to currency config successfully")
        void deserializeFromJson_shouldCreateValidConfig_whenValidJsonProvided() throws Exception {
            // Arrange
            String json = """
                {
                    "defaultCurrency": "EUR",
                    "supported": [
                        {
                            "code": "EUR",
                            "symbol": "€",
                            "name": "Euro",
                            "precision": 2
                        }
                    ],
                    "autoDetect": false,
                    "markup": 0.03
                }
                """;

            // Act
            CurrencyConfig deserializedConfig = objectMapper.readValue(json, CurrencyConfig.class);

            // Assert
            assertThat(deserializedConfig)
                    .returns("EUR", CurrencyConfig::getDefaultCurrency)
                    .returns(false, CurrencyConfig::isAutoDetect)
                    .returns(0.03, CurrencyConfig::getMarkup);

            assertThat(deserializedConfig.getSupported())
                    .hasSize(1)
                    .first()
                    .returns("EUR", CurrencyConfig.Currency::getCode)
                    .returns("€", CurrencyConfig.Currency::getSymbol);
        }

        @Test
        @DisplayName("Should handle missing optional fields during deserialization")
        void deserializeFromJson_shouldHandleMissingFields_whenOptionalFieldsOmitted() throws Exception {
            // Arrange
            String minimalJson = """
                {
                    "defaultCurrency": "ZAR"
                }
                """;

            // Act
            CurrencyConfig deserializedConfig = objectMapper.readValue(minimalJson, CurrencyConfig.class);

            // Assert
            assertThat(deserializedConfig.getDefaultCurrency()).isEqualTo("ZAR");
            assertThat(deserializedConfig.getSupported()).isNull();
            assertThat(deserializedConfig.getFormatting()).isNull();
        }
    }

    @Nested
    @DisplayName("Currency Object Properties")
    class CurrencyObjectTests {

        @Test
        @DisplayName("Should create currency with all properties")
        void createCurrency_shouldSetAllProperties_whenValidDataProvided() {
            // Arrange
            CurrencyConfig.Currency currency = new CurrencyConfig.Currency();

            // Act
            currency.setCode("ZAR");
            currency.setSymbol("R");
            currency.setName("South African Rand");
            currency.setPrecision(2);

            // Assert
            assertThat(currency)
                    .returns("ZAR", CurrencyConfig.Currency::getCode)
                    .returns("R", CurrencyConfig.Currency::getSymbol)
                    .returns("South African Rand", CurrencyConfig.Currency::getName)
                    .returns(2, CurrencyConfig.Currency::getPrecision);
        }

        @ParameterizedTest
        @CsvSource({
                "JPY, 0",
                "USD, 2",
                "NGN, 0",
                "EUR, 2",
                "KES, 0"
        })
        @DisplayName("Should set currency precision for different currency types")
        void setCurrencyPrecision_shouldAcceptValues_whenValidPrecisionProvided(String code, int precision) {
            // Arrange
            CurrencyConfig.Currency currency = new CurrencyConfig.Currency();

            // Act
            currency.setCode(code);
            currency.setPrecision(precision);

            // Assert
            assertThat(currency.getPrecision()).isEqualTo(precision);
        }
    }

    @Nested
    @DisplayName("Backup FX Providers Configuration")
    class BackupFxProvidersTests {

        @Test
        @DisplayName("Should set and get backup FX providers list")
        void setBackupFxProviders_shouldStoreProviders_whenValidListProvided() {
            // Arrange
            List<String> backupProviders = Arrays.asList("fixer.io", "currencyapi.com", "exchangerate-api.com");

            // Act
            currencyConfig.setBackupFxProviders(backupProviders);

            // Assert
            assertThat(currencyConfig.getBackupFxProviders())
                    .hasSize(3)
                    .containsExactly("fixer.io", "currencyapi.com", "exchangerate-api.com");
        }

        @Test
        @DisplayName("Should handle empty backup providers list")
        void setBackupFxProviders_shouldHandleEmptyList_whenNoProvidersProvided() {
            // Act
            currencyConfig.setBackupFxProviders(Arrays.asList());

            // Assert
            assertThat(currencyConfig.getBackupFxProviders()).isEmpty();
        }
    }

    @Nested
    @DisplayName("Edge Cases and Boundary Conditions")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should handle null values for all properties")
        void setPropertiesToNull_shouldNotThrowExceptions_whenNullValuesProvided() {
            // Act & Assert - Verify no exceptions are thrown
            assertDoesNotThrow(() -> {
                currencyConfig.setDefaultCurrency(null);
                currencyConfig.setSupported(null);
                currencyConfig.setFormatting(null);
                currencyConfig.setRegionalDefaults(null);
                currencyConfig.setBackupFxProviders(null);
            });

            // Verify all properties are null
            assertThat(currencyConfig.getDefaultCurrency()).isNull();
            assertThat(currencyConfig.getSupported()).isNull();
            assertThat(currencyConfig.getFormatting()).isNull();
            assertThat(currencyConfig.getRegionalDefaults()).isNull();
            assertThat(currencyConfig.getBackupFxProviders()).isNull();
        }

        @Test
        @DisplayName("Should handle currency with zero precision")
        void createCurrency_shouldHandleZeroPrecision_whenCurrencyHasNoDecimalPlaces() {
            // Arrange
            CurrencyConfig.Currency currency = createCurrency("JPY", "¥", "Japanese Yen", 0);

            // Assert
            assertThat(currency.getPrecision()).isZero();
        }
    }

    // Helper methods for test data creation
    private CurrencyConfig.Currency createCurrency(String code, String symbol, String name, int precision) {
        CurrencyConfig.Currency currency = new CurrencyConfig.Currency();
        currency.setCode(code);
        currency.setSymbol(symbol);
        currency.setName(name);
        currency.setPrecision(precision);
        return currency;
    }

    private CurrencyConfig.Formatting createFormatting(String thousandsSep, String decimalSep,
                                                       String symbolPos, boolean spaceBetween) {
        CurrencyConfig.Formatting formatting = new CurrencyConfig.Formatting();
        formatting.setThousandsSeparator(thousandsSep);
        formatting.setDecimalSeparator(decimalSep);
        formatting.setSymbolPosition(symbolPos);
        formatting.setSpaceBetweenSymbol(spaceBetween);
        return formatting;
    }
}
