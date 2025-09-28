package za.co.pms.util;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author NMMkhungo
 * @since 2025/09/24
 **/
@Component
public class SmartRegionDetector {
    private final Map<String, String> countryToRegion = new HashMap<>() {{
        put("ZA", "southAfrica");
        put("NA", "southAfrica");
        put("BW", "southAfrica");
        put("LS", "southAfrica");
        put("SZ", "southAfrica");
        put("NG", "westAfrica");
        put("GH", "westAfrica");
        put("CI", "westAfrica");
        put("SN", "westAfrica");
        put("KE", "eastAfrica");
        put("TZ", "eastAfrica");
        put("UG", "eastAfrica");
        put("RW", "eastAfrica");
        put("FR", "europe");
        put("DE", "europe");
        put("IT", "europe");
        put("ES", "europe");
        put("GB", "europe");
    }};

    public String detectRegion(String countryCode, String ipAddress) {
        // Primary: lookup from config map
        String configRegion = countryToRegion.get(countryCode);
        if (configRegion != null) {
            return configRegion;
        }

        // Fallback: geographic/IP-based detection
        return detectGeographicRegion(countryCode, ipAddress);
    }

    /**
     * Fallback region detection if the country code is not in the predefined map.
     * Currently returns "unknown" but can be extended to use a GeoIP service.
     */
    protected String detectGeographicRegion(String countryCode, String ipAddress) {
        // Placeholder implementation — avoids NullPointerExceptions
        if (countryCode == null || countryCode.isBlank()) {
            return "unknown";
        }

        // Simple fallback: guess region by first letter (demo only)
        switch (countryCode.charAt(0)) {
            case 'U': return "northAmerica"; // e.g., US
            case 'A': return "asia";         // e.g., AE, AR
            default:  return "unknown";
        }
    }
}
