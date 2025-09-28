package za.co.pms.dto;

import lombok.Value;

/**
 * @author NMMkhungo
 * @since 2025/09/24
 **/

@Value
public class FormattedAmount {
    String value;       // The actual formatted string, e.g. "$ 1,234.56"
    String type;        // "localeAware", "configBased", "fallback"

    public static FormattedAmount localeAware(String value) {
        return new FormattedAmount(value, "localeAware");
    }

    public static FormattedAmount configBased(String value) {
        return new FormattedAmount(value, "configBased");
    }

    public static FormattedAmount fallback(String value) {
        return new FormattedAmount(value, "fallback");
    }
}
