package za.co.pms.dto;

/**
 * @author NMMkhungo
 * @since 2025/09/24
 **/

import lombok.*;
import za.co.pms.config.CurrencyConfig;

import java.util.List;

/**
 * Represents the regional configuration context for a given country.
 * Includes currency, formatting, compliance rules, and payment methods.
 */
@Getter
@Builder
@ToString
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RegionalContext {
    /**
     * ISO country code (e.g., "ZA", "NG").
     */
    private final String countryCode;

    /**
     * Currency code used in the country (e.g., "ZAR", "NGN").
     */
    private final String currencyCode;

    /**
     * Formatting rules for the currency (thousands separator, decimal separator, symbol position, etc.).
     */
    private final CurrencyConfig.Formatting formattingRules;

    /**
     * Compliance rules applicable to this region (POPIA, GDPR, CBN guidelines, etc.).
     */
    private final CurrencyConfig.Compliance complianceRules;

    /**
     * Payment methods available in this region (cards, mobile money, bank transfers, etc.).
     */
    private final List<String> paymentMethods;
}
