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
import java.util.Collections;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

/**
 * @author NMMkhungo
 * @since 2025/09/24
 **/
@ExtendWith(MockitoExtension.class)
@DisplayName("CurrencyFormatting Unit Tests")
class CurrencyFormattingTest {
    @Mock
    private CurrencyConfig currencyConfig;

    private CurrencyFormatting currencyFormatting;

    private CurrencyConfig.Currency usdCurrency;
    private CurrencyConfig.Currency jpyCurrency;
    private CurrencyConfig.Formatting usdFormatting;
    private CurrencyConfig.Formatting eurFormatting;

    @BeforeEach
    void setUp() {
        currencyFormatting = new CurrencyFormatting(currencyConfig);
        initializeTestCurrencies();
        initializeTestFormattingRules();
    }

    @Nested
    @DisplayName("Amount Formatting with Valid Currency")
    class AmountFormattingTests {

        @BeforeEach
        void setUpUsdConfiguration() {
            when(currencyConfig.getSupported()).thenReturn(Collections.singletonList(usdCurrency));
            when(currencyConfig.getFormatting()).thenReturn(Collections.singletonMap("USD", usdFormatting));
        }

        @Test
        @DisplayName("Should format USD amount with symbol, thousands separator, and proper rounding")
        void formatAmount_shouldFormatCorrectly_whenValidUsdAmountProvided() {
            // Arrange
            BigDecimal amount = new BigDecimal("1234.567");

            // Act
            String result = currencyFormatting.formatAmount(amount, "USD");

            // Assert
            assertThat(result).isEqualTo("$ 1,234.57");
        }

        @Test
        @DisplayName("Should format zero amount correctly")
        void formatAmount_shouldFormatZero_whenZeroAmountProvided() {
            // Arrange
            BigDecimal zeroAmount = BigDecimal.ZERO;

            // Act
            String result = currencyFormatting.formatAmount(zeroAmount, "USD");

            // Assert
            assertThat(result).isEqualTo("$ 0.00");
        }

        @Test
        @DisplayName("Should format large amount with proper thousands separators")
        void formatAmount_shouldFormatLargeAmount_whenMillionAmountProvided() {
            // Arrange
            BigDecimal largeAmount = new BigDecimal("1234567.89");

            // Act
            String result = currencyFormatting.formatAmount(largeAmount, "USD");

            // Assert
            assertThat(result).isEqualTo("$ 1,234,567.89");
        }

        @ParameterizedTest
        @CsvSource({
                "1234.567, '$ 1,234.57'",
                "999.999, '$ 1,000.00'",
                "0.001, '$ 0.00'",
                "1234567.89, '$ 1,234,567.89'"
        })
        @DisplayName("Should format various USD amounts correctly")
        void formatAmount_shouldFormatVariousAmounts_whenDifferentValuesProvided(
                BigDecimal amount, String expectedResult) {
            // Act
            String result = currencyFormatting.formatAmount(amount, "USD");

            // Assert
            assertThat(result).isEqualTo(expectedResult);
        }
    }

    @Nested
    @DisplayName("Amount Formatting with Symbol After Amount")
    class SymbolAfterAmountTests {

        @BeforeEach
        void setUpEurConfiguration() {
            CurrencyConfig.Currency eurCurrency = createCurrency("EUR", "€", "Euro", 2);
            CurrencyConfig.Formatting eurFormatting = createFormatting(".", ",", "after", true);

            when(currencyConfig.getSupported()).thenReturn(Collections.singletonList(eurCurrency));
            when(currencyConfig.getFormatting()).thenReturn(Collections.singletonMap("EUR", eurFormatting));
        }

        @Test
        @DisplayName("Should format EUR amount with symbol after amount")
        void formatAmount_shouldPlaceSymbolAfter_whenEurCurrencyProvided() {
            // Arrange
            BigDecimal amount = new BigDecimal("1234.56");

            // Act
            String result = currencyFormatting.formatAmount(amount, "EUR");

            // Assert
            assertThat(result).isEqualTo("1.234,56 €");
        }
    }

    @Nested
    @DisplayName("Null and Invalid Input Handling")
    class NullAndInvalidInputTests {

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" ", "  "})
        @DisplayName("Should return empty string when currency code is null or blank")
        void formatAmount_shouldReturnEmptyString_whenCurrencyCodeIsNullOrBlank(String currencyCode) {
            // Arrange
            BigDecimal amount = new BigDecimal("100.50");

            // Act
            String result = currencyFormatting.formatAmount(amount, currencyCode);

            // Assert
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should return empty string when amount is null")
        void formatAmount_shouldReturnEmptyString_whenAmountIsNull() {
            // Act
            String result = currencyFormatting.formatAmount(null, "USD");

            // Assert
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should return empty string when both amount and currency code are null")
        void formatAmount_shouldReturnEmptyString_whenBothInputsAreNull() {
            // Act
            String result = currencyFormatting.formatAmount(null, null);

            // Assert
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("Unsupported Currency Handling")
    class UnsupportedCurrencyTests {

        @BeforeEach
        void setUpUnsupportedCurrency() {
            when(currencyConfig.getSupported()).thenReturn(Collections.singletonList(usdCurrency));
            when(currencyConfig.getFormatting()).thenReturn(Collections.singletonMap("USD", usdFormatting));
        }

        @Test
        @DisplayName("Should use fallback format for unsupported currency")
        void formatAmount_shouldUseFallbackFormat_whenUnsupportedCurrencyProvided() {
            // Arrange
            BigDecimal amount = new BigDecimal("100.50");

            // Act
            String result = currencyFormatting.formatAmount(amount, "XYZ");

            // Assert
            assertThat(result).isEqualTo("100.50 XYZ");
        }

        @Test
        @DisplayName("Should use fallback format when currency formatting rules are missing")
        void formatAmount_shouldUseFallbackFormat_whenFormattingRulesMissing() {
            // Arrange
            BigDecimal amount = new BigDecimal("100.50");

            // Act
            String result = currencyFormatting.formatAmount(amount, "EUR"); // EUR not in formatting map

            // Assert
            assertThat(result).isEqualTo("100.50 EUR");
        }
    }

    @Nested
    @DisplayName("Amount Only Formatting (Without Symbol)")
    class AmountOnlyFormattingTests {

        @BeforeEach
        void setUpConfiguration() {
            when(currencyConfig.getSupported()).thenReturn(Collections.singletonList(usdCurrency));
            when(currencyConfig.getFormatting()).thenReturn(Collections.singletonMap("USD", usdFormatting));
        }

        @Test
        @DisplayName("Should format amount without currency symbol")
        void formatAmountOnly_shouldReturnFormattedNumber_whenValidAmountProvided() {
            // Arrange
            BigDecimal amount = new BigDecimal("1234.567");

            // Act
            String result = currencyFormatting.formatAmountOnly(amount, "USD");

            // Assert
            assertThat(result).isEqualTo("1,234.57");
        }

        @Test
        @DisplayName("Should return amount string when formatting rules missing")
        void formatAmountOnly_shouldReturnPlainAmount_whenFormattingRulesMissing() {
            // Arrange
            BigDecimal amount = new BigDecimal("100.50");

            // Act
            String result = currencyFormatting.formatAmountOnly(amount, "EUR");

            // Assert
            assertThat(result).isEqualTo("100.50");
        }
    }

    @Nested
    @DisplayName("Amount Parsing from Formatted Strings")
    class AmountParsingTests {

        @BeforeEach
        void setUpConfiguration() {
            when(currencyConfig.getSupported()).thenReturn(Collections.singletonList(usdCurrency));
            when(currencyConfig.getFormatting()).thenReturn(Collections.singletonMap("USD", usdFormatting));
        }

        @Test
        @DisplayName("Should parse formatted currency string back to BigDecimal")
        void parseAmount_shouldReturnBigDecimal_whenValidFormattedStringProvided() {
            // Arrange
            String formattedAmount = "$ 1,234.56";

            // Act
            BigDecimal result = currencyFormatting.parseAmount(formattedAmount, "USD");

            // Assert
            assertThat(result).isEqualByComparingTo("1234.56");
        }

        @ParameterizedTest
        @CsvSource({
                "'$ 1,234.56', '1234.56'",
                "'1,234.56', '1234.56'",
                "'1234.56', '1234.56'",
                "'$1,234.56', '1234.56'"
        })
        @DisplayName("Should parse various formatted strings correctly")
        void parseAmount_shouldHandleDifferentFormats_whenVariousFormatsProvided(
                String formattedAmount, String expectedAmount) {
            // Act
            BigDecimal result = currencyFormatting.parseAmount(formattedAmount, "USD");

            // Assert
            assertThat(result).isEqualByComparingTo(expectedAmount);
        }

        @Test
        @DisplayName("Should return zero when parsing invalid string")
        void parseAmount_shouldReturnZero_whenInvalidStringProvided() {
            // Arrange
            String invalidAmount = "invalid amount";

            // Act
            BigDecimal result = currencyFormatting.parseAmount(invalidAmount, "USD");

            // Assert
            assertThat(result).isEqualByComparingTo(BigDecimal.ZERO);
        }

        @Test
        @DisplayName("Should return zero when parsing null or empty string")
        void parseAmount_shouldReturnZero_whenNullOrEmptyStringProvided() {
            // Act & Assert
            assertThat(currencyFormatting.parseAmount(null, "USD")).isEqualByComparingTo(BigDecimal.ZERO);
            assertThat(currencyFormatting.parseAmount("", "USD")).isEqualByComparingTo(BigDecimal.ZERO);
            assertThat(currencyFormatting.parseAmount("   ", "USD")).isEqualByComparingTo(BigDecimal.ZERO);
        }
    }

    @Nested
    @DisplayName("Currency Symbol and Position Utilities")
    class CurrencySymbolTests {

        @BeforeEach
        void setUpConfiguration() {
            when(currencyConfig.getSupported()).thenReturn(Collections.singletonList(usdCurrency));
            when(currencyConfig.getFormatting()).thenReturn(Collections.singletonMap("USD", usdFormatting));
        }

        @Test
        @DisplayName("Should return currency symbol for supported currency")
        void getCurrencySymbol_shouldReturnSymbol_whenSupportedCurrencyProvided() {
            // Act
            String symbol = currencyFormatting.getCurrencySymbol("USD");

            // Assert
            assertThat(symbol).isEqualTo("$");
        }

        @Test
        @DisplayName("Should return currency code when symbol not available")
        void getCurrencySymbol_shouldReturnCode_whenUnsupportedCurrencyProvided() {
            // Act
            String symbol = currencyFormatting.getCurrencySymbol("XYZ");

            // Assert
            assertThat(symbol).isEqualTo("XYZ");
        }

        @Test
        @DisplayName("Should detect symbol before amount position")
        void isSymbolBeforeAmount_shouldReturnTrue_whenSymbolPositionIsBefore() {
            // Act
            boolean isBefore = currencyFormatting.isSymbolBeforeAmount("USD");

            // Assert
            assertThat(isBefore).isTrue();
        }

        @Test
        @DisplayName("Should detect symbol after amount position")
        void isSymbolBeforeAmount_shouldReturnFalse_whenSymbolPositionIsAfter() {
            // Arrange
            CurrencyConfig.Formatting afterFormatting = createFormatting(".", ",", "after", true);
            when(currencyConfig.getFormatting()).thenReturn(Collections.singletonMap("EUR", afterFormatting));

            // Act
            boolean isBefore = currencyFormatting.isSymbolBeforeAmount("EUR");

            // Assert
            assertThat(isBefore).isFalse();
        }

        @Test
        @DisplayName("Should return true when formatting rules missing (default behavior)")
        void isSymbolBeforeAmount_shouldReturnTrue_whenFormattingRulesMissing() {
            // Act
            boolean isBefore = currencyFormatting.isSymbolBeforeAmount("XYZ");

            // Assert
            assertThat(isBefore).isTrue();
        }
    }

    @Nested
    @DisplayName("Currency with Zero Decimal Precision")
    class ZeroPrecisionCurrencyTests {

        @BeforeEach
        void setUpJpyConfiguration() {
            when(currencyConfig.getSupported()).thenReturn(Collections.singletonList(jpyCurrency));

            CurrencyConfig.Formatting jpyFormatting = createFormatting(",", ".", "before", false);
            when(currencyConfig.getFormatting()).thenReturn(Collections.singletonMap("JPY", jpyFormatting));
        }

        @Test
        @DisplayName("Should format JPY amount without decimal places")
        void formatAmount_shouldRoundToZeroDecimals_whenCurrencyHasZeroPrecision() {
            // Arrange
            BigDecimal amount = new BigDecimal("1234.567");

            // Act
            String result = currencyFormatting.formatAmount(amount, "JPY");

            // Assert
            assertThat(result).isEqualTo("¥1,235"); // Rounded to nearest integer
        }

        @Test
        @DisplayName("Should parse JPY amount without decimal places")
        void parseAmount_shouldHandleZeroPrecision_whenJapaneseYenProvided() {
            // Arrange
            String formattedAmount = "¥1,235";

            // Act
            BigDecimal result = currencyFormatting.parseAmount(formattedAmount, "JPY");

            // Assert
            assertThat(result).isEqualByComparingTo("1235");
        }
    }

    // Helper methods for test data creation
    private void initializeTestCurrencies() {
        usdCurrency = createCurrency("USD", "$", "US Dollar", 2);
        jpyCurrency = createCurrency("JPY", "¥", "Japanese Yen", 0);
    }

    private void initializeTestFormattingRules() {
        usdFormatting = createFormatting(",", ".", "before", true);
        eurFormatting = createFormatting(".", ",", "after", true);
    }

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
