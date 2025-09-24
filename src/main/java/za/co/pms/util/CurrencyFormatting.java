package za.co.pms.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import za.co.pms.config.CurrencyConfig;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * @author NMMkhungo
 * @since 2025/09/23
 * Utility class for formatting currency amounts according to locale-specific rules
 */

@Component
@Slf4j
public class CurrencyFormatting {
    private final CurrencyConfig currencyConfig;

    public CurrencyFormatting(CurrencyConfig currencyConfig) {
        this.currencyConfig = currencyConfig;
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
