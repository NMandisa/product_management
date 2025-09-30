package za.co.pms.enums;

/**
 * @author NMMkhungo
 * @since 2025/09/28
 **/
public enum Region {
    // Your existing constants with added data
    SOUTHERN_AFRICA("Southern Africa", "ZA, NA, BW"),
    WEST_AFRICA("West Africa", "NG, GH, CI"),
    EAST_AFRICA("East Africa", "KE, TZ, UG"),
    NORTH_AFRICA("North Africa", "EG, LY, DZ"),
    CENTRAL_AFRICA("Central Africa", "CD, AO, CM"),
    ASIA("Asia", "CN, IN, JP"),
    EUROPE("Europe", "DE, FR, GB"),
    SOUTHERN_AMERICA("South America", "BR, AR, CL"),
    NORTHERN_AMERICA("North America", "US, CA, MX"),
    GLOBAL_RESERVE("Global Reserve Currencies", "US, EU, CN"),
    EMERGING_MARKETS("Emerging Markets", "BR, RU, IN");

    // Fields to hold the data
    private final String displayName;
    private final String countryExamples;

    // Constructor to initialize the fields
    Region(String displayName, String countryExamples) {
        this.displayName = displayName;
        this.countryExamples = countryExamples;
    }

    // Public accessor methods
    public String getDisplayName() {
        return displayName;
    }

    public String getCountryExamples() {
        return countryExamples;
    }

    // A useful method to look up an enum by its display name
    public static Region fromDisplayName(String displayName) {
        for (Region region : values()) {
            if (region.getDisplayName().equals(displayName)) {
                return region;
            }
        }
        throw new IllegalArgumentException("No region found for: " + displayName);
    }
}
