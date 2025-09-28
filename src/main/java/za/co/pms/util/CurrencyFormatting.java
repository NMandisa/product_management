package za.co.pms.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import za.co.pms.config.CurrencyConfig;
import za.co.pms.dto.FormattedAmount;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author NMMkhungo
 * @since 2025/09/23
 * Utility component for formatting and parsing monetary amounts
 * using both locale-aware rules and a master currency configuration.
 *
 * <p>This class provides multiple formatting strategies:</p>
 * <ul>
 *     <li><b>Locale-aware:</b> Uses {@link java.text.NumberFormat} for built-in locale formatting.</li>
 *     <li><b>Config-based:</b> Falls back to custom {@link CurrencyConfig} rules when locale fails.</li>
 *     <li><b>Fallback:</b> Gracefully degrades to plain string concatenation if no rules exist.</li>
 * </ul>
 *
 * <p>Thread-safe caching of {@link NumberFormat} is maintained via {@link ConcurrentHashMap}
 * for performance in repeated formatting operations.</p>
 */

@Component
@Slf4j
public class CurrencyFormatting {
    private final CurrencyConfig currencyConfig;
    private final Map<Locale, NumberFormat> localeFormats = new ConcurrentHashMap<>();

    public CurrencyFormatting(CurrencyConfig currencyConfig) {
        this.currencyConfig = currencyConfig;
    }

    /**
     * Format amount using locale-aware rules first, falling back to
     * config-based rules if locale formatting fails.
     *
     * @param amount       the monetary value
     * @param currencyCode ISO 4217 currency code (e.g. "USD", "EUR")
     * @param locale       the target {@link Locale}
     * @return a {@link FormattedAmount} containing the formatted string and origin
     */
    public FormattedAmount formatWithMasterConfig(BigDecimal amount, String currencyCode, Locale locale) {
        // 1. Try locale-aware formatting first
        try {
            NumberFormat format = localeFormats.computeIfAbsent(locale,
                    NumberFormat::getCurrencyInstance);
            format.setCurrency(Currency.getInstance(currencyCode));
            return FormattedAmount.localeAware(format.format(amount));
        } catch (Exception e) {
            log.warn("Locale-aware formatting failed for {} in {}. Falling back to config rules.", currencyCode, locale, e);
            // 2. Fallback to master config formatting
            return formatWithConfigRules(amount, currencyCode);
        }
    }

    /**
     * Format amount using rules from {@link CurrencyConfig}.
     *
     * @param amount       the monetary value
     * @param currencyCode ISO 4217 currency code
     * @return formatted amount, or fallback if rules missing
     */
    private FormattedAmount formatWithConfigRules(BigDecimal amount, String currencyCode) {
        CurrencyConfig.Currency currency = getCurrency(currencyCode);
        CurrencyConfig.Formatting formatting = currencyConfig.getFormatting().get(currencyCode);

        if (currency == null || formatting == null) {
            log.warn("No formatting rules found for {}. Using fallback.", currencyCode);
            return FormattedAmount.fallback(amount + " " + currencyCode);
        }

        String formattedNumber = formatNumber(amount, currency.getPrecision(), formatting);
        return buildFormattedAmount(amount, currency, formatting, formattedNumber);
    }

    private String formatNumber(BigDecimal amount, int precision, CurrencyConfig.Formatting formatting) {
        BigDecimal scaled = amount.setScale(precision, RoundingMode.HALF_UP);

        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator(formatting.getDecimalSeparator().charAt(0));
        symbols.setGroupingSeparator(formatting.getThousandsSeparator().charAt(0));

        StringBuilder pattern = new StringBuilder("#,##0");
        if (precision > 0) {
            pattern.append(".");
            pattern.append("0".repeat(precision));
        }

        DecimalFormat decimalFormat = new DecimalFormat(pattern.toString(), symbols);
        return decimalFormat.format(scaled);
    }

    private FormattedAmount buildFormattedAmount(BigDecimal amount,
                                                 CurrencyConfig.Currency currency,
                                                 CurrencyConfig.Formatting formatting,
                                                 String formattedNumber) {
        String symbol = currency.getSymbol();
        String result;

        if ("before".equalsIgnoreCase(formatting.getSymbolPosition())) {
            result = symbol + (formatting.isSpaceBetweenSymbol() ? " " : "") + formattedNumber;
        } else {
            result = formattedNumber + (formatting.isSpaceBetweenSymbol() ? " " : "") + symbol;
        }

        return FormattedAmount.configBased(result);
    }
    /**
     * Format amount with currency symbol and proper formatting
     */
    public String formatAmount(BigDecimal amount, String currencyCode) {
        if (amount == null || currencyCode == null) {
            return "";
        }

        CurrencyConfig.Currency currency = getCurrency(currencyCode);
        CurrencyConfig.Formatting formatting = getFormatting(currencyCode);

        if (currency == null || formatting == null) {
            return fallbackFormat(amount, currencyCode);
        }

        BigDecimal roundedAmount = roundAmount(amount, currency.getPrecision());
        return buildFormattedString(roundedAmount, currency, formatting);
    }

    /**
     * Format amount for a specific locale
     */
    public String formatAmount(BigDecimal amount, String currencyCode, Locale locale) {
        if (locale == null) {
            return formatAmount(amount, currencyCode);
        }

        try {
            // Use Java's built-in currency formatting for the locale
            java.text.NumberFormat format = java.text.NumberFormat.getCurrencyInstance(locale);
            format.setCurrency(java.util.Currency.getInstance(currencyCode));
            format.setMinimumFractionDigits(getCurrency(currencyCode).getPrecision());
            format.setMaximumFractionDigits(getCurrency(currencyCode).getPrecision());

            return format.format(amount);
        } catch (Exception e) {
            log.warn("Failed to format currency for locale {}, using default formatting", locale, e);
            return formatAmount(amount, currencyCode);
        }
    }

    /**
     * Format amount without currency symbol
     */
    public String formatAmountOnly(BigDecimal amount, String currencyCode) {
        if (amount == null || currencyCode == null) return "";

        CurrencyConfig.Currency currency = getCurrency(currencyCode);
        CurrencyConfig.Formatting formatting = getFormatting(currencyCode);

        if (currency == null || formatting == null) {
            return amount.toString();
        }

        BigDecimal roundedAmount = roundAmount(amount, currency.getPrecision());
        return formatNumber(roundedAmount, formatting);
    }

    /**
     * Parse formatted currency string back to BigDecimal
     */
    public BigDecimal parseAmount(String formattedAmount, String currencyCode) {
        if (formattedAmount == null || formattedAmount.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }

        try {
            // Remove currency symbol and spaces
            String cleanAmount = formattedAmount.trim()
                    .replaceAll("[^\\d.,\\s-]", "") // Remove non-numeric except , . - and space
                    .replaceAll("\\s", ""); // Remove spaces

            CurrencyConfig.Formatting formatting = getFormatting(currencyCode);
            if (formatting != null) {
                // Replace the decimal separator with standard dot
                cleanAmount = cleanAmount.replace(formatting.getDecimalSeparator(), ".");
                // Remove thousands separators
                cleanAmount = cleanAmount.replace(formatting.getThousandsSeparator(), "");
            }

            return new BigDecimal(cleanAmount);
        } catch (Exception e) {
            log.error("Failed to parse amount: {}", formattedAmount, e);
            return BigDecimal.ZERO;
        }
    }

    /**
     * Get currency symbol for a currency code
     */
    public String getCurrencySymbol(String currencyCode) {
        CurrencyConfig.Currency currency = getCurrency(currencyCode);
        return currency != null ? currency.getSymbol() : currencyCode;
    }

    /**
     * Check if currency symbol should be placed before amount
     */
    public boolean isSymbolBeforeAmount(String currencyCode) {
        CurrencyConfig.Formatting formatting = getFormatting(currencyCode);
        return formatting != null && "before".equals(formatting.getSymbolPosition());
    }

    private CurrencyConfig.Currency getCurrency(String currencyCode) {
        return currencyConfig.getSupported().stream()
                .filter(c -> c.getCode().equalsIgnoreCase(currencyCode))
                .findFirst()
                .orElse(null);
    }

    private CurrencyConfig.Formatting getFormatting(String currencyCode) {
        return currencyConfig.getFormatting().get(currencyCode.toUpperCase());
    }

    private BigDecimal roundAmount(BigDecimal amount, int precision) {
        return amount.setScale(precision, RoundingMode.HALF_UP);
    }

    private String buildFormattedString(BigDecimal amount, CurrencyConfig.Currency currency,
                                        CurrencyConfig.Formatting formatting) {
        String numberStr = formatNumber(amount, formatting);

        if ("before".equals(formatting.getSymbolPosition())) {
            return currency.getSymbol() +
                    (formatting.isSpaceBetweenSymbol() ? " " : "") +
                    numberStr;
        } else {
            return numberStr +
                    (formatting.isSpaceBetweenSymbol() ? " " : "") +
                    currency.getSymbol();
        }
    }

    private String formatNumber(BigDecimal number, CurrencyConfig.Formatting formatting) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setDecimalSeparator(formatting.getDecimalSeparator().charAt(0));
        symbols.setGroupingSeparator(formatting.getThousandsSeparator().charAt(0));

        DecimalFormat formatter = new DecimalFormat();
        formatter.setDecimalFormatSymbols(symbols);
        formatter.setGroupingSize(3);
        formatter.setGroupingUsed(true);
        formatter.setMinimumFractionDigits(0);
        formatter.setMaximumFractionDigits(10); // Will be limited by rounding

        return formatter.format(number);
    }

    private String fallbackFormat(BigDecimal amount, String currencyCode) {
        return amount.toString() + " " + currencyCode;
    }
}
