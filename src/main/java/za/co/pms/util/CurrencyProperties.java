package za.co.pms.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import za.co.pms.config.CurrencyConfig;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author NMMkhungo
 * @since 2025/09/23
 * Manages currency properties and regional defaults
 */
@Component
@Slf4j
public class CurrencyProperties {
    private final CurrencyConfig currencyConfig;

    public CurrencyProperties(CurrencyConfig currencyConfig) {
        this.currencyConfig = currencyConfig;
    }

    /**
     * Get all supported currency codes
     */
    public List<String> getSupportedCurrencyCodes() {
        return currencyConfig.getSupported().stream()
                .map(CurrencyConfig.Currency::getCode)
                .collect(Collectors.toList());
    }

    /**
     * Get currency details by code
     */
    public Optional<CurrencyConfig.Currency> getCurrency(String currencyCode) {
        return currencyConfig.getSupported().stream()
                .filter(c -> c.getCode().equalsIgnoreCase(currencyCode))
                .findFirst();
    }

    /**
     * Get default currency for a country code
     */
    public String getDefaultCurrencyForCountry(String countryCode) {
        String currency = currencyConfig.getRegionalDefaults().get(countryCode.toUpperCase());
        return currency != null ? currency : currencyConfig.getDefaultCurrency();
    }

    /**
     * Get default currency
     */
    public String getDefaultCurrency() {
        return currencyConfig.getDefaultCurrency();
    }

    /**
     * Check if currency is supported
     */
    public boolean isCurrencySupported(String currencyCode) {
        return currencyConfig.getSupported().stream()
                .anyMatch(c -> c.getCode().equalsIgnoreCase(currencyCode));
    }

    /**
     * Get formatting rules for currency
     */
    public Optional<CurrencyConfig.Formatting> getFormattingRules(String currencyCode) {
        return Optional.ofNullable(currencyConfig.getFormatting().get(currencyCode.toUpperCase()));
    }

    /**
     * Get all countries that use a specific currency
     */
    public List<String> getCountriesForCurrency(String currencyCode) {
        return currencyConfig.getRegionalDefaults().entrySet().stream()
                .filter(entry -> entry.getValue().equalsIgnoreCase(currencyCode))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    /**
     * Get currency precision (decimal places)
     */
    public int getPrecision(String currencyCode) {
        return getCurrency(currencyCode)
                .map(CurrencyConfig.Currency::getPrecision)
                .orElse(2); // Default to 2 decimal places
    }

    /**
     * Validate amount for currency precision
     */
    public boolean isValidAmount(BigDecimal amount, String currencyCode) {
        if (amount == null) return false;

        int precision = getPrecision(currencyCode);
        try {
            amount.setScale(precision, RoundingMode.UNNECESSARY);
            return true;
        } catch (ArithmeticException e) {
            return false; // Too many decimal places
        }
    }

    /**
     * Round amount to currency's precision
     */
    public BigDecimal roundToCurrencyPrecision(BigDecimal amount, String currencyCode) {
        if (amount == null) return BigDecimal.ZERO;

        int precision = getPrecision(currencyCode);
        return amount.setScale(precision, RoundingMode.HALF_UP);
    }

    /**
     * Get currencies by region
     */
    public Map<String, List<CurrencyConfig.Currency>> getCurrenciesByRegion() {
        Map<String, List<CurrencyConfig.Currency>> regionalCurrencies = new HashMap<>();

        // Group by currency usage patterns
        regionalCurrencies.put("AFRICA", getAfricanCurrencies());
        regionalCurrencies.put("EUROPE", getEuropeanCurrencies());
        regionalCurrencies.put("ASIA", getAsianCurrencies());
        regionalCurrencies.put("AMERICAS", getAmericanCurrencies());

        return regionalCurrencies;
    }

    /**
     * Get popular currencies (most commonly used)
     */
    public List<CurrencyConfig.Currency> getPopularCurrencies() {
        // Return the most commonly used currencies first
        List<String> popularCodes = Arrays.asList("ZAR", "USD", "EUR", "GBP", "NGN", "KES");

        return popularCodes.stream()
                .map(this::getCurrency)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    /**
     * Get FX markup percentage
     */
    public BigDecimal getFxMarkup() {
        return BigDecimal.valueOf(currencyConfig.getMarkup());
    }

    /**
     * Calculate amount with FX markup
     */
    public BigDecimal applyFxMarkup(BigDecimal amount) {
        BigDecimal markup = getFxMarkup();
        return amount.multiply(BigDecimal.ONE.add(markup));
    }

    private List<CurrencyConfig.Currency> getAfricanCurrencies() {
        return filterCurrenciesByCodes("ZAR", "NGN", "KES", "BWP", "MZN", "TZS", "ZMW", "GHS", "EGP", "XOF", "XAF", "RWF", "UGX");
    }

    private List<CurrencyConfig.Currency> getEuropeanCurrencies() {
        return filterCurrenciesByCodes("EUR", "GBP", "CHF");
    }

    private List<CurrencyConfig.Currency> getAsianCurrencies() {
        return filterCurrenciesByCodes("CNY", "JPY", "INR", "AED", "SAR");
    }

    private List<CurrencyConfig.Currency> getAmericanCurrencies() {
        return filterCurrenciesByCodes("USD", "CAD", "BRL", "AUD");
    }

    private List<CurrencyConfig.Currency> filterCurrenciesByCodes(String... codes) {
        return Arrays.stream(codes)
                .map(this::getCurrency)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }
}
