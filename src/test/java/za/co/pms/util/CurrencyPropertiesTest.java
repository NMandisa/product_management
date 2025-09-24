package za.co.pms.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import za.co.pms.config.CurrencyConfig;

import java.math.BigDecimal;
import java.util.*;

import static org.mockito.Mockito.when;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

/**
 * @author NMMkhungo
 * @since 2025/09/24
 **/

@ExtendWith(MockitoExtension.class)
@DisplayName("CurrencyProperties Unit Tests")
class CurrencyPropertiesTest {
    @Mock
    private CurrencyConfig currencyConfig;

    private CurrencyProperties currencyProperties;

    private CurrencyConfig.Currency usdCurrency;
    private CurrencyConfig.Currency zarCurrency;
    private CurrencyConfig.Currency jpyCurrency;
    private CurrencyConfig.Currency eurCurrency;

    @BeforeEach
    void setUp() {
        currencyProperties = new CurrencyProperties(currencyConfig);
        initializeTestCurrencies();
        configureDefaultMockBehavior();
    }

    @Nested
    @DisplayName("Currency Support and Retrieval")
    class CurrencySupportTests {

        @Test
        @DisplayName("Should return list of supported currency codes")
        void getSupportedCurrencyCodes_shouldReturnAllCurrencyCodes_whenCurrenciesConfigured() {
            // Act
            List<String> supportedCodes = currencyProperties.getSupportedCurrencyCodes();

            // Assert
            assertThat(supportedCodes)
                    .hasSize(4)
                    .containsExactly("USD", "ZAR", "JPY", "EUR");
        }

        @Test
        @DisplayName("Should return empty list when no currencies are supported")
        void getSupportedCurrencyCodes_shouldReturnEmptyList_whenNoCurrenciesConfigured() {
            // Arrange
            when(currencyConfig.getSupported()).thenReturn(Collections.emptyList());

            // Act
            List<String> supportedCodes = currencyProperties.getSupportedCurrencyCodes();

            // Assert
            assertThat(supportedCodes).isEmpty();
        }

        @Test
        @DisplayName("Should return currency details when currency exists")
        void getCurrency_shouldReturnCurrency_whenSupportedCurrencyCodeProvided() {
            // Act
            Optional<CurrencyConfig.Currency> result = currencyProperties.getCurrency("USD");

            // Assert
            assertThat(result)
                    .isPresent()
                    .get()
                    .returns("USD", CurrencyConfig.Currency::getCode)
                    .returns(2, CurrencyConfig.Currency::getPrecision);
        }

        @ParameterizedTest
        @ValueSource(strings = {"XYZ", "GBP", "CAD"})
        @NullAndEmptySource
        @DisplayName("Should return empty optional for unsupported or invalid currency codes")
        void getCurrency_shouldReturnEmptyOptional_whenUnsupportedCurrencyProvided(String currencyCode) {
            // Act
            Optional<CurrencyConfig.Currency> result = currencyProperties.getCurrency(currencyCode);

            // Assert
            assertThat(result).isEmpty();
        }

        @ParameterizedTest
        @CsvSource({
                "USD, true",
                "ZAR, true",
                "EUR, true",
                "XYZ, false",
                "GBP, false"
        })
        @DisplayName("Should correctly identify supported currencies")
        void isCurrencySupported_shouldReturnCorrectStatus_whenCurrencyCodeProvided(
                String currencyCode, boolean expectedSupported) {
            // Act
            boolean isSupported = currencyProperties.isCurrencySupported(currencyCode);

            // Assert
            assertThat(isSupported).isEqualTo(expectedSupported);
        }
    }

    @Nested
    @DisplayName("Regional Defaults and Country Mapping")
    class RegionalDefaultsTests {

        @BeforeEach
        void setUpRegionalDefaults() {
            Map<String, String> regionalDefaults = Map.of(
                    "ZA", "ZAR",
                    "US", "USD",
                    "NG", "NGN",
                    "KE", "KES",
                    "GB", "GBP",
                    "FR", "EUR",
                    "DE", "EUR"
            );
            when(currencyConfig.getRegionalDefaults()).thenReturn(regionalDefaults);
        }

        @Test
        @DisplayName("Should return correct default currency for country code")
        void getDefaultCurrencyForCountry_shouldReturnCurrency_whenCountryHasRegionalDefault() {
            // Act & Assert
            assertAll(
                    () -> assertThat(currencyProperties.getDefaultCurrencyForCountry("ZA")).isEqualTo("ZAR"),
                    () -> assertThat(currencyProperties.getDefaultCurrencyForCountry("US")).isEqualTo("USD"),
                    () -> assertThat(currencyProperties.getDefaultCurrencyForCountry("FR")).isEqualTo("EUR")
            );
        }

        @Test
        @DisplayName("Should return global default when country has no regional default")
        void getDefaultCurrencyForCountry_shouldReturnGlobalDefault_whenCountryNotInRegionalDefaults() {
            // Act
            String result = currencyProperties.getDefaultCurrencyForCountry("CA");

            // Assert
            assertThat(result).isEqualTo("USD"); // Global default
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" ", "  "})
        @DisplayName("Should return global default when country code is null or blank")
        void getDefaultCurrencyForCountry_shouldReturnGlobalDefault_whenCountryCodeInvalid(String countryCode) {
            // Act
            String result = currencyProperties.getDefaultCurrencyForCountry(countryCode);

            // Assert
            assertThat(result).isEqualTo("USD");
        }

        @Test
        @DisplayName("Should return countries that use a specific currency")
        void getCountriesForCurrency_shouldReturnCountryList_whenCurrencyUsedByMultipleCountries() {
            // Act
            List<String> countries = currencyProperties.getCountriesForCurrency("EUR");

            // Assert
            assertThat(countries)
                    .hasSize(2)
                    .containsExactlyInAnyOrder("FR", "DE");
        }

        @Test
        @DisplayName("Should return empty list when currency not used by any country")
        void getCountriesForCurrency_shouldReturnEmptyList_whenCurrencyNotInRegionalDefaults() {
            // Act
            List<String> countries = currencyProperties.getCountriesForCurrency("CAD");

            // Assert
            assertThat(countries).isEmpty();
        }
    }

    @Nested
    @DisplayName("Currency Precision and Amount Validation")
    class CurrencyPrecisionTests {

        @Test
        @DisplayName("Should return correct precision for different currencies")
        void getPrecision_shouldReturnPrecision_whenCurrencyCodeProvided() {
            // Act & Assert
            assertAll(
                    () -> assertThat(currencyProperties.getPrecision("USD")).isEqualTo(2),
                    () -> assertThat(currencyProperties.getPrecision("JPY")).isEqualTo(0),
                    () -> assertThat(currencyProperties.getPrecision("ZAR")).isEqualTo(2)
            );
        }

        @Test
        @DisplayName("Should return default precision for unsupported currency")
        void getPrecision_shouldReturnDefaultPrecision_whenUnsupportedCurrencyProvided() {
            // Act
            int precision = currencyProperties.getPrecision("XYZ");

            // Assert
            assertThat(precision).isEqualTo(2); // Default precision
        }

        @ParameterizedTest
        @CsvSource({
                "100.00, USD, true",
                "100.0, USD, true",
                "100, USD, true",
                "100.001, USD, false",
                "100.0001, USD, false",
                "100, JPY, true",
                "100.0, JPY, false",
                "100.00, JPY, false"
        })
        @DisplayName("Should validate amount precision against currency requirements")
        void isValidAmount_shouldValidatePrecision_whenAmountAndCurrencyProvided(
                BigDecimal amount, String currencyCode, boolean expectedValid) {
            // Act
            boolean isValid = currencyProperties.isValidAmount(amount, currencyCode);

            // Assert
            assertThat(isValid).isEqualTo(expectedValid);
        }

        @Test
        @DisplayName("Should return false when amount is null")
        void isValidAmount_shouldReturnFalse_whenAmountIsNull() {
            // Act
            boolean isValid = currencyProperties.isValidAmount(null, "USD");

            // Assert
            assertThat(isValid).isFalse();
        }

        @Test
        @DisplayName("Should round amount to currency precision")
        void roundToCurrencyPrecision_shouldRoundCorrectly_whenAmountProvided() {
            // Act & Assert
            assertAll(
                    () -> assertThat(currencyProperties.roundToCurrencyPrecision(new BigDecimal("123.4567"), "USD"))
                            .isEqualByComparingTo("123.46"),
                    () -> assertThat(currencyProperties.roundToCurrencyPrecision(new BigDecimal("123.451"), "USD"))
                            .isEqualByComparingTo("123.45"),
                    () -> assertThat(currencyProperties.roundToCurrencyPrecision(new BigDecimal("123.4567"), "JPY"))
                            .isEqualByComparingTo("123"),
                    () -> assertThat(currencyProperties.roundToCurrencyPrecision(new BigDecimal("123.5"), "JPY"))
                            .isEqualByComparingTo("124")
            );
        }

        @Test
        @DisplayName("Should return zero when rounding null amount")
        void roundToCurrencyPrecision_shouldReturnZero_whenAmountIsNull() {
            // Act
            BigDecimal result = currencyProperties.roundToCurrencyPrecision(null, "USD");

            // Assert
            assertThat(result).isEqualByComparingTo(BigDecimal.ZERO);
        }
    }

    @Nested
    @DisplayName("FX Markup Calculations")
    class FxMarkupTests {

        @Test
        @DisplayName("Should return correct FX markup percentage")
        void getFxMarkup_shouldReturnMarkupPercentage_whenConfigured() {
            // Act
            BigDecimal markup = currencyProperties.getFxMarkup();

            // Assert
            assertThat(markup).isEqualByComparingTo("0.05");
        }

        @ParameterizedTest
        @CsvSource({
                "100.00, 105.00",
                "50.00, 52.50",
                "0.00, 0.00",
                "1000.00, 1050.00"
        })
        @DisplayName("Should apply FX markup to amount correctly")
        void applyFxMarkup_shouldCalculateMarkup_whenAmountProvided(
                BigDecimal amount, BigDecimal expectedAmount) {
            // Act
            BigDecimal result = currencyProperties.applyFxMarkup(amount);

            // Assert
            assertThat(result).isEqualByComparingTo(expectedAmount);
        }

        @Test
        @DisplayName("Should handle null amount in markup calculation")
        void applyFxMarkup_shouldReturnZero_whenAmountIsNull() {
            // Act
            BigDecimal result = currencyProperties.applyFxMarkup(null);

            // Assert
            assertThat(result).isEqualByComparingTo(BigDecimal.ZERO);
        }
    }

    @Nested
    @DisplayName("Currency Grouping and Regional Organization")
    class CurrencyGroupingTests {

        @Test
        @DisplayName("Should group currencies by region correctly")
        void getCurrenciesByRegion_shouldReturnRegionalGroups_whenMultipleCurrenciesExist() {
            // Act
            Map<String, List<CurrencyConfig.Currency>> regionalCurrencies =
                    currencyProperties.getCurrenciesByRegion();

            // Assert
            assertThat(regionalCurrencies)
                    .containsKeys("AFRICA", "EUROPE", "ASIA", "AMERICAS");

            assertThat(regionalCurrencies.get("AFRICA"))
                    .extracting(CurrencyConfig.Currency::getCode)
                    .contains("ZAR");

            assertThat(regionalCurrencies.get("EUROPE"))
                    .extracting(CurrencyConfig.Currency::getCode)
                    .contains("EUR");

            assertThat(regionalCurrencies.get("ASIA"))
                    .extracting(CurrencyConfig.Currency::getCode)
                    .contains("JPY");

            assertThat(regionalCurrencies.get("AMERICAS"))
                    .extracting(CurrencyConfig.Currency::getCode)
                    .contains("USD");
        }

        @Test
        @DisplayName("Should return popular currencies in correct order")
        void getPopularCurrencies_shouldReturnOrderedList_whenPopularCurrenciesDefined() {
            // Act
            List<CurrencyConfig.Currency> popularCurrencies = currencyProperties.getPopularCurrencies();

            // Assert
            assertThat(popularCurrencies)
                    .extracting(CurrencyConfig.Currency::getCode)
                    .containsExactly("ZAR", "USD", "EUR", "GBP", "NGN", "KES");
        }
    }

    @Nested
    @DisplayName("Default Currency Configuration")
    class DefaultCurrencyTests {

        @Test
        @DisplayName("Should return configured default currency")
        void getDefaultCurrency_shouldReturnDefault_whenConfigured() {
            // Act
            String defaultCurrency = currencyProperties.getDefaultCurrency();

            // Assert
            assertThat(defaultCurrency).isEqualTo("USD");
        }

        @Test
        @DisplayName("Should handle null default currency configuration")
        void getDefaultCurrency_shouldHandleNull_whenDefaultNotConfigured() {
            // Arrange
            when(currencyConfig.getDefaultCurrency()).thenReturn(null);

            // Act
            String defaultCurrency = currencyProperties.getDefaultCurrency();

            // Assert
            assertThat(defaultCurrency).isNull();
        }
    }

    @Nested
    @DisplayName("Edge Cases and Error Handling")
    class EdgeCaseTests {

        @Test
        @DisplayName("Should handle null configuration gracefully")
        void allMethods_shouldHandleNullConfiguration_whenConfigIsNull() {
            // Arrange
            CurrencyProperties propertiesWithNullConfig = new CurrencyProperties(null);

            // Act & Assert
            assertAll(
                    () -> assertThat(propertiesWithNullConfig.getSupportedCurrencyCodes()).isEmpty(),
                    () -> assertThat(propertiesWithNullConfig.getDefaultCurrency()).isNull(),
                    () -> assertThat(propertiesWithNullConfig.getCurrency("USD")).isEmpty(),
                    () -> assertThat(propertiesWithNullConfig.isCurrencySupported("USD")).isFalse(),
                    () -> assertThat(propertiesWithNullConfig.getPrecision("USD")).isEqualTo(2),
                    () -> assertThat(propertiesWithNullConfig.isValidAmount(BigDecimal.TEN, "USD")).isFalse(),
                    () -> assertThat(propertiesWithNullConfig.roundToCurrencyPrecision(BigDecimal.TEN, "USD"))
                            .isEqualByComparingTo(BigDecimal.TEN),
                    () -> assertThat(propertiesWithNullConfig.getFxMarkup())
                            .isEqualByComparingTo(BigDecimal.ZERO)
            );
        }

        @Test
        @DisplayName("Should handle null supported currencies list")
        void getSupportedCurrencyCodes_shouldHandleNull_whenSupportedCurrenciesNull() {
            // Arrange
            when(currencyConfig.getSupported()).thenReturn(null);

            // Act
            List<String> supportedCodes = currencyProperties.getSupportedCurrencyCodes();

            // Assert
            assertThat(supportedCodes).isEmpty();
        }
    }

    // Helper methods for test setup
    private void initializeTestCurrencies() {
        usdCurrency = createCurrency("USD", "$", "US Dollar", 2);
        zarCurrency = createCurrency("ZAR", "R", "South African Rand", 2);
        jpyCurrency = createCurrency("JPY", "¥", "Japanese Yen", 0);
        eurCurrency = createCurrency("EUR", "€", "Euro", 2);
    }

    private void configureDefaultMockBehavior() {
        when(currencyConfig.getSupported()).thenReturn(Arrays.asList(
                usdCurrency, zarCurrency, jpyCurrency, eurCurrency
        ));

        when(currencyConfig.getRegionalDefaults()).thenReturn(Map.of("ZA", "ZAR", "US", "USD"));
        when(currencyConfig.getDefaultCurrency()).thenReturn("USD");
        when(currencyConfig.getMarkup()).thenReturn(0.05);
    }

    private CurrencyConfig.Currency createCurrency(String code, String symbol, String name, int precision) {
        CurrencyConfig.Currency currency = new CurrencyConfig.Currency();
        currency.setCode(code);
        currency.setSymbol(symbol);
        currency.setName(name);
        currency.setPrecision(precision);
        return currency;
    }
}
