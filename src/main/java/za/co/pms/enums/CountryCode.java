package za.co.pms.enums;

import lombok.Getter;

import java.util.EnumSet;
import java.util.Set;

/**
 * @author NMMkhungo
 * @since 2025/09/30
 * ISO 3166-1 alpha-2 country codes with regional groupings
 * Provides type-safe country code handling with regional business logic
 **/

public enum CountryCode {
    // Southern Africa
    ZA("South Africa", Region.SOUTHERN_AFRICA),
    NA("Namibia", Region.SOUTHERN_AFRICA),
    BW("Botswana", Region.SOUTHERN_AFRICA),
    LS("Lesotho", Region.SOUTHERN_AFRICA),
    SZ("Eswatini", Region.SOUTHERN_AFRICA),
    MZ("Mozambique", Region.SOUTHERN_AFRICA),
    ZM("Zambia", Region.SOUTHERN_AFRICA),
    ZW("Zimbabwe", Region.SOUTHERN_AFRICA),

    // West Africa
    NG("Nigeria", Region.WEST_AFRICA),
    GH("Ghana", Region.WEST_AFRICA),
    CI("Côte d'Ivoire", Region.WEST_AFRICA),
    SN("Senegal", Region.WEST_AFRICA),
    GN("Guinea", Region.WEST_AFRICA),
    TG("Togo", Region.WEST_AFRICA),
    LR("Liberia", Region.WEST_AFRICA),
    SL("Sierra Leone", Region.WEST_AFRICA),

    // East Africa
    KE("Kenya", Region.EAST_AFRICA),
    UG("Uganda", Region.EAST_AFRICA),
    TZ("Tanzania", Region.EAST_AFRICA),
    RW("Rwanda", Region.EAST_AFRICA),
    BI("Burundi", Region.EAST_AFRICA),
    ET("Ethiopia", Region.EAST_AFRICA),
    SO("Somalia", Region.EAST_AFRICA),

    // Europe
    FR("France", Region.EUROPE),
    DE("Germany", Region.EUROPE),
    IT("Italy", Region.EUROPE),
    ES("Spain", Region.EUROPE),
    GB("United Kingdom", Region.EUROPE),
    NL("Netherlands", Region.EUROPE),
    BE("Belgium", Region.EUROPE),
    PT("Portugal", Region.EUROPE);

    @Getter
    private final String countryName;
    @Getter
    private final Region region;

    CountryCode(String countryName, Region region) {
        this.countryName = countryName;
        this.region = region;
    }

    public String getIsoCode() {
        return name(); // Returns the enum name (ZA, NG, etc.)
    }

    // Regional groupings using EnumSet for performance
    private static final Set<CountryCode> SADC_COUNTRIES = EnumSet.of(
            ZA, NA, BW, LS, SZ, MZ, ZM, ZW
    );

    private static final Set<CountryCode> ECOWAS_COUNTRIES = EnumSet.of(
            NG, GH, CI, SN, GN, TG, LR, SL
    );

    private static final Set<CountryCode> EUROPEAN_UNION = EnumSet.of(
            FR, DE, IT, ES, NL, BE, PT
    );

    // Business logic methods
    public boolean isSADC() {
        return SADC_COUNTRIES.contains(this);
    }

    public boolean isECOWAS() {
        return ECOWAS_COUNTRIES.contains(this);
    }

    public boolean isEuropeanUnion() {
        return EUROPEAN_UNION.contains(this);
    }

    public boolean isAfricanCountry() {
        return this.region != Region.EUROPE &&
                this.region != Region.NORTHERN_AMERICA &&
                this.region != Region.ASIA;
    }

    // Lookup methods
    public static CountryCode fromString(String code) {
        try {
            return CountryCode.valueOf(code.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid country code: " + code);
        }
    }

    public static Set<CountryCode> getCountriesInRegion(Region region) {
        Set<CountryCode> result = EnumSet.noneOf(CountryCode.class);
        for (CountryCode country : values()) {
            if (country.getRegion() == region) {
                result.add(country);
            }
        }
        return result;
    }
}
