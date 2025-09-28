package za.co.pms.util.validator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import za.co.pms.config.CurrencyConfig;

import java.util.Currency;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author NMMkhungo
 * @since 2025/09/24
 **/
@Slf4j
@Component
public class CurrencyConfigValidator {
    /**
     * Perform all master configuration validations.
     * @param config The CurrencyConfig to validate.
     */
    public void validateMasterConfig(CurrencyConfig config) {
        if (config == null) {
            log.error("CurrencyConfig is null. Cannot validate.");
            return;
        }

        validateIsoCompliance(config);
        validateRegionalCoverage(config);
        validateFormattingConsistency(config);
    }

    /**
     * Validate that all supported currencies conform to ISO 4217.
     * @param config Currency configuration to check.
     */
    private void validateIsoCompliance(CurrencyConfig config) {
        // Get the set of all ISO-compliant currency codes
        Set<String> isoCurrencies = Currency.getAvailableCurrencies().stream()
                .map(Currency::getCurrencyCode)
                .collect(Collectors.toSet());

        if (config.getSupported() == null || config.getSupported().isEmpty()) {
            log.warn("No supported currencies defined in config.");
            return;
        }

        // Warn if any currency in config is not ISO-compliant
        config.getSupported().forEach(currency -> {
            if (currency.getCode() == null || !isoCurrencies.contains(currency.getCode())) {
                log.warn("Non-ISO currency code in config: {}", currency.getCode());
            }
        });
    }

    /**
     * Validate that the config covers required regional currencies.
     * @param config Currency configuration to check.
     */
    private void validateRegionalCoverage(CurrencyConfig config) {
        // TODO: Implement logic to ensure that all regions have at least one supported currency
        // For now, we just log that this validation needs to be done.
        log.debug("validateRegionalCoverage not yet implemented");
    }

    /**
     * Validate that formatting rules are consistent.
     * @param config Currency configuration to check.
     */
    private void validateFormattingConsistency(CurrencyConfig config) {
        // TODO: Implement logic to check thousands/decimal separators and symbol positions
        log.debug("validateFormattingConsistency not yet implemented");
    }
}
