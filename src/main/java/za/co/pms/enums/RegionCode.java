package za.co.pms.enums;


import lombok.Getter;

import java.util.Map;
import java.util.Set;

/**
 * @author NMMkhungo
 * @since 2025/09/30
 **/
@Getter
public enum RegionCode {
    // Southern Africa
    ZA("South Africa", "ZA", Region.SOUTHERN_AFRICA, "Africa/Johannesburg", "+27"),
    NA("Namibia", "NA", Region.SOUTHERN_AFRICA, "Africa/Windhoek", "+264"),
    BW("Botswana", "BW", Region.SOUTHERN_AFRICA, "Africa/Gaborone", "+267"),
    LS("Lesotho", "LS", Region.SOUTHERN_AFRICA, "Africa/Maseru", "+266"),
    SZ("Eswatini", "SZ", Region.SOUTHERN_AFRICA, "Africa/Mbabane", "+268"),
    MZ("Mozambique", "MZ", Region.SOUTHERN_AFRICA, "Africa/Maputo", "+258"),
    ZM("Zambia", "ZM", Region.SOUTHERN_AFRICA, "Africa/Lusaka", "+260"),
    ZW("Zimbabwe", "ZW", Region.SOUTHERN_AFRICA, "Africa/Harare", "+263"),

    // West Africa
    NG("Nigeria", "NG", Region.WEST_AFRICA, "Africa/Lagos", "+234"),
    GH("Ghana", "GH", Region.WEST_AFRICA, "Africa/Accra", "+233"),
    CI("Côte d'Ivoire", "CI", Region.WEST_AFRICA, "Africa/Abidjan", "+225"),
    SN("Senegal", "SN", Region.WEST_AFRICA, "Africa/Dakar", "+221"),
    GN("Guinea", "GN", Region.WEST_AFRICA, "Africa/Conakry", "+224"),
    ML("Mali", "ML", Region.WEST_AFRICA, "Africa/Bamako", "+223"),
    BF("Burkina Faso", "BF", Region.WEST_AFRICA, "Africa/Ouagadougou", "+226"),
    BJ("Benin", "BJ", Region.WEST_AFRICA, "Africa/Porto-Novo", "+229"),
    NE("Niger", "NE", Region.WEST_AFRICA, "Africa/Niamey", "+227"),
    TG("Togo", "TG", Region.WEST_AFRICA, "Africa/Lome", "+228"),
    LR("Liberia", "LR", Region.WEST_AFRICA, "Africa/Monrovia", "+231"),
    SL("Sierra Leone", "SL", Region.WEST_AFRICA, "Africa/Freetown", "+232"),
    GM("Gambia", "GM", Region.WEST_AFRICA, "Africa/Banjul", "+220"),

    // East Africa
    KE("Kenya", "KE", Region.EAST_AFRICA, "Africa/Nairobi", "+254"),
    TZ("Tanzania", "TZ", Region.EAST_AFRICA, "Africa/Dar_es_Salaam", "+255"),
    UG("Uganda", "UG", Region.EAST_AFRICA, "Africa/Kampala", "+256"),
    ET("Ethiopia", "ET", Region.EAST_AFRICA, "Africa/Addis_Ababa", "+251"),
    RW("Rwanda", "RW", Region.EAST_AFRICA, "Africa/Kigali", "+250"),
    BI("Burundi", "BI", Region.EAST_AFRICA, "Africa/Bujumbura", "+257"),
    SS("South Sudan", "SS", Region.EAST_AFRICA, "Africa/Juba", "+211"),
    DJ("Djibouti", "DJ", Region.EAST_AFRICA, "Africa/Djibouti", "+253"),
    ER("Eritrea", "ER", Region.EAST_AFRICA, "Africa/Asmara", "+291"),
    SO("Somalia", "SO", Region.EAST_AFRICA, "Africa/Mogadishu", "+252"),

    // North Africa
    EG("Egypt", "EG", Region.NORTH_AFRICA, "Africa/Cairo", "+20"),
    MA("Morocco", "MA", Region.NORTH_AFRICA, "Africa/Casablanca", "+212"),
    DZ("Algeria", "DZ", Region.NORTH_AFRICA, "Africa/Algiers", "+213"),
    TN("Tunisia", "TN", Region.NORTH_AFRICA, "Africa/Tunis", "+216"),
    LY("Libya", "LY", Region.NORTH_AFRICA, "Africa/Tripoli", "+218"),
    SD("Sudan", "SD", Region.NORTH_AFRICA, "Africa/Khartoum", "+249"),

    // Central Africa
    CD("Democratic Republic of the Congo", "CD", Region.CENTRAL_AFRICA, "Africa/Kinshasa", "+243"),
    CG("Republic of the Congo", "CG", Region.CENTRAL_AFRICA, "Africa/Brazzaville", "+242"),
    AO("Angola", "AO", Region.CENTRAL_AFRICA, "Africa/Luanda", "+244"),
    CM("Cameroon", "CM", Region.CENTRAL_AFRICA, "Africa/Douala", "+237"),
    GA("Gabon", "GA", Region.CENTRAL_AFRICA, "Africa/Libreville", "+241"),
    GQ("Equatorial Guinea", "GQ", Region.CENTRAL_AFRICA, "Africa/Malabo", "+240"),
    TD("Chad", "TD", Region.CENTRAL_AFRICA, "Africa/Ndjamena", "+235"),
    CF("Central African Republic", "CF", Region.CENTRAL_AFRICA, "Africa/Bangui", "+236"),

    // Global Reserve Currencies (Non-African but needed for FX)
    US("United States", "US", Region.GLOBAL_RESERVE, "America/New_York", "+1"),
    GB("United Kingdom", "GB", Region.GLOBAL_RESERVE, "Europe/London", "+44"),
    EU("European Union", "EU", Region.GLOBAL_RESERVE, "Europe/Brussels", "+32"),
    JP("Japan", "JP", Region.GLOBAL_RESERVE, "Asia/Tokyo", "+81"),
    CH("Switzerland", "CH", Region.GLOBAL_RESERVE, "Europe/Zurich", "+41"),
    CN("China", "CN", Region.GLOBAL_RESERVE, "Asia/Shanghai", "+86"),
    CA("Canada", "CA", Region.GLOBAL_RESERVE, "America/Toronto", "+1"),
    AU("Australia", "AU", Region.GLOBAL_RESERVE, "Australia/Sydney", "+61"),
    DE("Germany", "DE", Region.EUROPE, "Europe/Berlin", "+49"),
    ES("Spain", "ES", Region.EUROPE, "Europe/Madrid", "+34"),
    FR("France", "FR", Region.EUROPE, "Europe/Paris", "+33"),

    // Emerging Markets (Non-African)
    BR("Brazil", "BR", Region.EMERGING_MARKETS, "America/Sao_Paulo", "+55"),
    IN("India", "IN", Region.EMERGING_MARKETS, "Asia/Kolkata", "+91"),
    RU("Russia", "RU", Region.EMERGING_MARKETS, "Europe/Moscow", "+7"),
    MX("Mexico", "MX", Region.EMERGING_MARKETS, "America/Mexico_City", "+52"),
    ID("Indonesia", "ID", Region.EMERGING_MARKETS, "Asia/Jakarta", "+62"),
    TR("Turkey", "TR", Region.EMERGING_MARKETS, "Europe/Istanbul", "+90"),
    SA("Saudi Arabia", "SA", Region.EMERGING_MARKETS, "Asia/Riyadh", "+966"),
    AE("United Arab Emirates", "AE", Region.EMERGING_MARKETS, "Asia/Dubai", "+971");

    // Getters
    @Getter
    private final String countryName;
    @Getter
    private final String isoCode;
    @Getter
    private final Region region;
    @Getter
    private final String timezone;
    @Getter
    private final String phoneCode;

    RegionCode(String countryName, String isoCode, Region region, String timezone, String phoneCode) {
        this.countryName = countryName;
        this.isoCode = isoCode;
        this.region = region;
        this.timezone = timezone;
        this.phoneCode = phoneCode;
    }

    // Static lookup methods
    public static RegionCode fromIsoCode(String isoCode) {
        for (RegionCode region : values()) {
            if (region.getIsoCode().equalsIgnoreCase(isoCode)) {
                return region;
            }
        }
        throw new IllegalArgumentException("Unknown ISO code: " + isoCode);
    }

    public static RegionCode fromCountryName(String countryName) {
        for (RegionCode region : values()) {
            if (region.getCountryName().equalsIgnoreCase(countryName)) {
                return region;
            }
        }
        throw new IllegalArgumentException("Unknown country name: " + countryName);
    }

    // Regional grouping methods
    public static java.util.List<RegionCode> getByRegion(Region currencyRegion) {
        return java.util.Arrays.stream(values())
                .filter(region -> region.getRegion() == currencyRegion)
                .collect(java.util.stream.Collectors.toList());
    }

    public static java.util.List<RegionCode> getAfricanRegions() {
        return java.util.Arrays.stream(values())
                .filter(region -> region.getRegion() != Region.GLOBAL_RESERVE &&
                        region.getRegion() != Region.EMERGING_MARKETS)
                .collect(java.util.stream.Collectors.toList());
    }

    public static java.util.List<RegionCode> getSADCRegions() {
        // Southern African Development Community
        java.util.Set<String> sadcCountries = java.util.Set.of(
                "ZA", "NA", "BW", "LS", "SZ", "MZ", "ZM", "ZW",
                "AO", "CD", "MG", "MW", "MU", "SC", "TZ"
        );
        return java.util.Arrays.stream(values())
                .filter(region -> sadcCountries.contains(region.getIsoCode()))
                .collect(java.util.stream.Collectors.toList());
    }

    public static java.util.List<RegionCode> getECOWASRegions() {
        // Economic Community of West African States
        java.util.Set<String> ecowasCountries = java.util.Set.of(
                "NG", "GH", "CI", "SN", "GN", "ML", "BF", "BJ", "NE", "TG",
                "LR", "SL", "GW", "GM", "CV"
        );
        return java.util.Arrays.stream(values())
                .filter(region -> ecowasCountries.contains(region.getIsoCode()))
                .collect(java.util.stream.Collectors.toList());
    }

    public static java.util.List<RegionCode> getEACRegions() {
        // East African Community
        java.util.Set<String> eacCountries = java.util.Set.of("KE", "TZ", "UG", "RW", "BI", "SS");
        return java.util.Arrays.stream(values())
                .filter(region -> eacCountries.contains(region.getIsoCode()))
                .collect(java.util.stream.Collectors.toList());
    }

    // Business logic methods
    public boolean isSADC() {
        return getSADCRegions().contains(this);
    }

    public boolean isECOWAS() {
        return getECOWASRegions().contains(this);
    }

    public boolean isEAC() {
        return getEACRegions().contains(this);
    }

    public boolean requiresVAT() {
        // Countries that require VAT/GST
        java.util.Set<String> vatCountries = java.util.Set.of(
                "ZA", "NG", "KE", "GH", "TZ", "UG", "RW", "ZM", "BW", "NA",
                "MZ", "LS", "SZ", "CM", "SN", "CI", "ML"
        );
        return vatCountries.contains(isoCode);
    }

    public java.math.BigDecimal getDefaultVATRate() {
        // Default VAT rates for countries
        java.util.Map<String, java.math.BigDecimal> vatRates = java.util.Map.ofEntries(
                Map.entry("ZA", new java.math.BigDecimal("15.00")),
                Map.entry("NG", new java.math.BigDecimal("7.50")),
                Map.entry("KE", new java.math.BigDecimal("16.00")),
                Map.entry("GH", new java.math.BigDecimal("12.50")),
                Map.entry("TZ", new java.math.BigDecimal("18.00")),
                Map.entry("UG", new java.math.BigDecimal("18.00")),
                Map.entry("RW", new java.math.BigDecimal("18.00")),
                Map.entry("ZM", new java.math.BigDecimal("16.00")),
                Map.entry("BW", new java.math.BigDecimal("12.00")),
                Map.entry("NA", new java.math.BigDecimal("15.00")),
                Map.entry("MZ", new java.math.BigDecimal("17.00")),
                Map.entry("LS", new java.math.BigDecimal("15.00")),
                Map.entry("SZ", new java.math.BigDecimal("15.00")),
                Map.entry("CM", new java.math.BigDecimal("19.25")),
                Map.entry("SN", new java.math.BigDecimal("18.00")),
                Map.entry("CI", new java.math.BigDecimal("18.00")),
                Map.entry("ML", new java.math.BigDecimal("18.00"))
        );
        return vatRates.getOrDefault(isoCode, java.math.BigDecimal.ZERO);
    }

    public boolean hasDigitalTax() {
        // Countries with digital services taxes
        Set<String> digitalTaxCountries = Set.of("ZA", "KE", "NG", "GH");
        return digitalTaxCountries.contains(isoCode);
    }

    public String getDefaultCurrencyCode() {
        // Default currency codes for countries
        Map<String, String> currencyCodes = Map.ofEntries(
                Map.entry("ZA", "ZAR"), Map.entry("NA", "NAD"), Map.entry("BW", "BWP"),
                Map.entry("LS", "LSL"), Map.entry("SZ", "SZL"), Map.entry("MZ", "MZN"),
                Map.entry("ZM", "ZMW"), Map.entry("ZW", "USD"), // Zimbabwe uses USD
                Map.entry("NG", "NGN"), Map.entry("GH", "GHS"), Map.entry("CI", "XOF"),
                Map.entry("SN", "XOF"), Map.entry("GN", "GNF"), Map.entry("ML", "XOF"),
                Map.entry("BF", "XOF"), Map.entry("BJ", "XOF"), Map.entry("NE", "XOF"),
                Map.entry("TG", "XOF"), Map.entry("LR", "LRD"), Map.entry("SL", "SLL"),
                Map.entry("KE", "KES"), Map.entry("TZ", "TZS"), Map.entry("UG", "UGX"),
                Map.entry("ET", "ETB"), Map.entry("RW", "RWF"), Map.entry("BI", "BIF"),
                Map.entry("SS", "SSP"), Map.entry("DJ", "DJF"), Map.entry("ER", "ERN"),
                Map.entry("SO", "SOS"), Map.entry("EG", "EGP"), Map.entry("MA", "MAD"),
                Map.entry("DZ", "DZD"), Map.entry("TN", "TND"), Map.entry("LY", "LYD"),
                Map.entry("SD", "SDG"), Map.entry("CD", "CDF"), Map.entry("CG", "XAF"),
                Map.entry("AO", "AOA"), Map.entry("CM", "XAF"), Map.entry("GA", "XAF"),
                Map.entry("GQ", "XAF"), Map.entry("TD", "XAF"), Map.entry("CF", "XAF")
        );
        return currencyCodes.getOrDefault(isoCode, "USD");
    }

    @Override
    public String toString() {
        return countryName + " (" + isoCode + ")";
    }
}
