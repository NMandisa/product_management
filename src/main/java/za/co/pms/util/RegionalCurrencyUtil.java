package za.co.pms.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author NMMkhungo
 * @since 2025/09/24
 **/
@Slf4j
@Component
public class RegionalCurrencyUtil {

    //private final CurrencyConfig masterConfig;

    /**
     * Constructor injecting the master currency configuration.
     * @param masterConfig Master CurrencyConfig bean.
     */
    /*public RegionalCurrencyUtil(CurrencyConfig masterConfig) {
        this.masterConfig = masterConfig;
    }*/

    /**
     * Builds a regional context for a given country code.
     * Includes currency, formatting, compliance, and payment methods.
     * @param countryCode ISO country code (e.g., "ZA", "NG").
     * @return RegionalContext object containing all regional settings.
     */
    /*public RegionalContext getRegionalContext(String countryCode) {
        if (countryCode == null || masterConfig == null) {
            throw new IllegalArgumentException("Country code and masterConfig cannot be null");
        }

        // Determine currency: country-specific or fallback to default
        String currencyCode = masterConfig.getRegionalDefaults() != null
                ? masterConfig.getRegionalDefaults().getOrDefault(countryCode, masterConfig.getDefaultCurrency())
                : masterConfig.getDefaultCurrency();

        return RegionalContext.builder()
                .countryCode(countryCode)
                .currencyCode(currencyCode)
                .formattingRules(masterConfig.getFormatting() != null
                        ? masterConfig.getFormatting().get(currencyCode)
                        : null)
                .complianceRules(getRegionalCompliance(countryCode))
                .paymentMethods(getRegionalPaymentMethods(countryCode))
                .build();
    }*/

    /**
     * Maps a country code to its regional compliance rules.
     * @param countryCode ISO country code
     * @return Compliance rules for the region
     */
   /* private CurrencyConfig.Compliance getRegionalCompliance(String countryCode) {
        if (masterConfig.getCompliance() == null) return null;*/

//        switch (countryCode) {
//            case "ZA": return masterConfig.getCompliance().getSouthAfrica();
//            case "NG": return masterConfig.getCompliance().getNigeria();
//            case "KE": return masterConfig.getCompliance().getKenya();
//            default:
//                return isEuropeanCountry(countryCode)
//                        ? masterConfig.getCompliance().getEurope()
//                        : masterConfig.getCompliance().getGlobal();
//        }
     /*   return null;
    }*/

    /**
     * Returns payment methods for a region based on country code.
     * Extend logic if your mapping needs to cover more countries.
     * @param countryCode ISO country code
     * @return List of payment methods
     */
    /*List<String> getRegionalPaymentMethods(String countryCode) {
        if (masterConfig.getPaymentMethods() == null) return Collections.emptyList();

        switch (countryCode) {
            case "ZA": return masterConfig.getPaymentMethods().getSouthAfrica();
            case "NG": case "GH": case "CI": case "SN": return masterConfig.getPaymentMethods().getWestAfrica();
            case "KE": case "UG": case "TZ": case "RW": return masterConfig.getPaymentMethods().getEastAfrica();
            case "FR": case "DE": case "IT": case "ES": case "GB": return masterConfig.getPaymentMethods().getEurope();
            default: return masterConfig.getPaymentMethods().getGlobal();
        }
    }*/

    /**
     * Determines if a country code belongs to Europe.
     * @param countryCode ISO country code
     * @return true if European, false otherwise
     */
    private boolean isEuropeanCountry(String countryCode) {
        return Arrays.asList("FR", "DE", "IT", "ES", "GB").contains(countryCode);
    }
}
