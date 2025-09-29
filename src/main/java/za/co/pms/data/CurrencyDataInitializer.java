package za.co.pms.data;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import za.co.pms.enums.Region;
import za.co.pms.enums.CurrencyStatus;
import za.co.pms.model.settings.Currency;
import za.co.pms.model.settings.currency.*;
import za.co.pms.repository.*;

import java.util.HashSet;
import java.util.Set;

/**
 * @author NMMkhungo
 * @since 2025/09/28
 **/
@Component
@RequiredArgsConstructor
public class CurrencyDataInitializer {

    private final CurrencyConfigRepository currencyConfigRepository;
    private final CurrencyRepository currencyRepository;
    private final CurrencyFormattingRepository currencyFormattingRepository;
    private final CurrencyFxRulesRepository currencyFxRulesRepository;
    private final AllowedCurrencyCrossRepository allowedCurrencyCrossRepository;
    private final FxConfigurationRepository fxConfigurationRepository;
    private final CircuitBreakerConfigRepository circuitBreakerConfigRepository;
    private final FxBackupSourceRepository fxBackupSourceRepository;
    private final RegionalDefaultRepository regionalDefaultRepository;
    private final AdvancedFeaturesRepository advancedFeaturesRepository;
    private final RegionalHierarchyRepository regionalHierarchyRepository;

    @PostConstruct
    @Transactional
    public void initialize() {
        if (currencyConfigRepository.count() == 0) {
            initializeFxConfiguration();
            initializeCurrencies();
            initializeAdvancedFeatures();
            initializeRegionalHierarchy();
            initializeCurrencyConfig();
        }
    }

    private void initializeCurrencyConfig() {
        FxConfiguration fxConfig = fxConfigurationRepository.findAll().get(0);
        AdvancedFeatures advancedFeatures = advancedFeaturesRepository.findAll().get(0);

        // Get the Currency entity for ZAR
        Currency defaultCurrency = currencyRepository.findByCurrencyCode("ZAR")
                .orElseThrow(() -> new RuntimeException("Default currency ZAR not found"));

        CurrencyConfig currencyConfig = new CurrencyConfig();
        currencyConfig.setDefaultCurrency(defaultCurrency); // Set the Currency entity
        currencyConfig.setFxConfiguration(fxConfig);
        currencyConfig.setAdvancedFeatures(advancedFeatures);

        // Save the config first
        CurrencyConfig savedConfig = currencyConfigRepository.save(currencyConfig);

        // Initialize regional defaults with the saved config
        initializeRegionalDefaults(savedConfig);
    }

    private void initializeRegionalDefaults(CurrencyConfig currencyConfig) {
        Set<RegionalDefault> regionalDefaults = new HashSet<>();

        // Create regional defaults for each country
        String[][] countryCurrencyPairs = {
                {"ZA", "ZAR"}, {"NG", "NGN"}, {"KE", "KES"}, {"GH", "GHS"},
                {"BW", "BWP"}, {"MZ", "MZN"}, {"TZ", "TZS"}, {"ZM", "ZMW"},
                {"MW", "MWK"}, {"NA", "NAD"}, {"EG", "EGP"}, {"SN", "XOF"},
                {"CI", "XOF"}, {"RW", "RWF"}, {"UG", "UGX"}, {"US", "USD"},
                {"GB", "GBP"}, {"FR", "EUR"}, {"CN", "CNY"}, {"IN", "INR"},
                {"global", "USD"}
        };

        for (String[] pair : countryCurrencyPairs) {
            RegionalDefault regionalDefault = new RegionalDefault();
            regionalDefault.setCountryCode(pair[0]);
            regionalDefault.setCurrencyCode(pair[1]);
            regionalDefault.setConfig(currencyConfig);
            regionalDefaults.add(regionalDefault);
        }

        regionalDefaultRepository.saveAll(regionalDefaults);

        // Update the currency config with the regional defaults
        currencyConfig.setRegionalDefaults(regionalDefaults);
        currencyConfigRepository.save(currencyConfig);
    }

    private void initializeFxConfiguration() {
        // Initialize circuit breaker
        CircuitBreakerConfig circuitBreaker = new CircuitBreakerConfig();
        circuitBreaker.setFailureThreshold(5);
        circuitBreaker.setResetTimeout(300000L);
        circuitBreakerConfigRepository.save(circuitBreaker);

        // Initialize FX configuration
        FxConfiguration fxConfig = new FxConfiguration();
        fxConfig.setPrimarySource("openexchangerates.org");
        fxConfig.setMarkup(0.05);
        fxConfig.setCacheTimeoutMinutes(30);
        fxConfig.setRetryAttempts(3);
        fxConfig.addCircuitBreakerConfig(circuitBreaker);

        // Initialize backup sources
        Set<FxBackupSource> backupSources = Set.of(
                new FxBackupSource("fixer.io", fxConfig),
                new FxBackupSource("currencyapi.com", fxConfig),
                new FxBackupSource("africafxcouncil.org", fxConfig)
        );
        fxConfig.setBackupSources(backupSources);

        fxConfigurationRepository.save(fxConfig);
    }

    private void initializeCurrencies() {
        // ZAR - South African Rand
        Currency zar = new Currency();
        zar.setCode("ZAR");
        zar.setSymbol("R");
        zar.setName("South African Rand");
        zar.setPrecision(2);
        zar.setComplianceRef("southAfrica");
        zar.setStatus(CurrencyStatus.ACTIVE);
        zar.setPriority(1);
        zar.setRegion(Region.SOUTHERN_AFRICA);
        zar.setCentralBank("SARB");
        zar.setDigitalMaturity("HIGH");

        // ZAR Formatting
        CurrencyFormatting zarFormatting = new CurrencyFormatting();
        zarFormatting.setCurrency(zar);
        zarFormatting.setThousandsSeparator(" ");
        zarFormatting.setDecimalSeparator(".");
        zarFormatting.setSymbolPosition("before");
        zarFormatting.setSpaceAfterSymbol(true);
        zarFormatting.setNegativeFormat("-{symbol}{amount}");
        zar.setFormatting(zarFormatting);

        // ZAR FX Rules
        CurrencyFxRules zarFxRules = new CurrencyFxRules();
        zarFxRules.setCurrency(zar);
        zarFxRules.setDefaultRateSource("SARB");
        zarFxRules.setCrossBorderRestrictions(true);
        zarFxRules.setMaxDailyLimit(1000000.0);
        zarFxRules.setVolatilityThreshold(0.05);

        Set<AllowedCurrencyCross> zarCrosses = Set.of(
                new AllowedCurrencyCross(zarFxRules, "USD"),
                new AllowedCurrencyCross(zarFxRules, "EUR"),
                new AllowedCurrencyCross(zarFxRules, "GBP"),
                new AllowedCurrencyCross(zarFxRules, "NGN"),
                new AllowedCurrencyCross(zarFxRules, "KES"),
                new AllowedCurrencyCross(zarFxRules, "BWP")
        );
        zarFxRules.setAllowedCrosses(zarCrosses);
        zar.setFxRules(zarFxRules);

        currencyRepository.save(zar);

        // Initialize other currencies (NGN, KES, GHS, USD) similarly...
        initializeNGN();
        initializeKES();
        initializeGHS();
        initializeUSD();
    }

    private void initializeNGN() {
        Currency ngn = new Currency();
        ngn.setCode("NGN");
        ngn.setSymbol("₦");
        ngn.setName("Nigerian Naira");
        ngn.setPrecision(0);
        ngn.setComplianceRef("nigeria");
        ngn.setStatus(CurrencyStatus.ACTIVE);
        ngn.setPriority(2);
        ngn.setRegion(Region.WEST_AFRICA);
        ngn.setCentralBank("CBN");
        ngn.setDigitalMaturity("HIGH");

        CurrencyFormatting ngnFormatting = new CurrencyFormatting();
        ngnFormatting.setCurrency(ngn);
        ngnFormatting.setThousandsSeparator(",");
        ngnFormatting.setDecimalSeparator(".");
        ngnFormatting.setSymbolPosition("before");
        ngnFormatting.setSpaceAfterSymbol(false);
        ngn.setFormatting(ngnFormatting);

        CurrencyFxRules ngnFxRules = new CurrencyFxRules();
        ngnFxRules.setCurrency(ngn);
        ngnFxRules.setDefaultRateSource("CBN");
        ngnFxRules.setCrossBorderRestrictions(true);
        ngnFxRules.setMaxDailyLimit(10000.0);
        ngnFxRules.setVolatilityThreshold(0.03);

        Set<AllowedCurrencyCross> ngnCrosses = Set.of(
                new AllowedCurrencyCross(ngnFxRules, "USD"),
                new AllowedCurrencyCross(ngnFxRules, "EUR"),
                new AllowedCurrencyCross(ngnFxRules, "GBP"),
                new AllowedCurrencyCross(ngnFxRules, "ZAR"),
                new AllowedCurrencyCross(ngnFxRules, "GHS")
        );
        ngnFxRules.setAllowedCrosses(ngnCrosses);
        ngn.setFxRules(ngnFxRules);

        currencyRepository.save(ngn);
    }

    private void initializeKES() {
        Currency kes = new Currency();
        kes.setCode("KES");
        kes.setSymbol("KSh");
        kes.setName("Kenyan Shilling");
        kes.setPrecision(0);
        kes.setComplianceRef("kenya");
        kes.setStatus(CurrencyStatus.ACTIVE);
        kes.setPriority(3);
        kes.setRegion(Region.EAST_AFRICA);
        kes.setCentralBank("CBK");
        kes.setDigitalMaturity("HIGH");

        CurrencyFormatting kesFormatting = new CurrencyFormatting();
        kesFormatting.setCurrency(kes);
        kesFormatting.setThousandsSeparator(",");
        kesFormatting.setDecimalSeparator(".");
        kesFormatting.setSymbolPosition("before");
        kesFormatting.setSpaceAfterSymbol(false);
        kes.setFormatting(kesFormatting);

        CurrencyFxRules kesFxRules = new CurrencyFxRules();
        kesFxRules.setCurrency(kes);
        kesFxRules.setDefaultRateSource("CBK");
        kesFxRules.setCrossBorderRestrictions(true);
        kesFxRules.setMaxDailyLimit(500000.0);
        kesFxRules.setVolatilityThreshold(0.04);

        Set<AllowedCurrencyCross> kesCrosses = Set.of(
                new AllowedCurrencyCross(kesFxRules, "USD"),
                new AllowedCurrencyCross(kesFxRules, "EUR"),
                new AllowedCurrencyCross(kesFxRules, "ZAR"),
                new AllowedCurrencyCross(kesFxRules, "UGX"),
                new AllowedCurrencyCross(kesFxRules, "TZS")
        );
        kesFxRules.setAllowedCrosses(kesCrosses);
        kes.setFxRules(kesFxRules);

        currencyRepository.save(kes);
    }

    private void initializeGHS() {
        Currency ghs = new Currency();
        ghs.setCode("GHS");
        ghs.setSymbol("GH₵");
        ghs.setName("Ghanaian Cedi");
        ghs.setPrecision(2);
        ghs.setComplianceRef("ghana");
        ghs.setStatus(CurrencyStatus.ACTIVE);
        ghs.setPriority(4);
        ghs.setRegion(Region.WEST_AFRICA);
        ghs.setCentralBank("BOG");

        CurrencyFormatting ghsFormatting = new CurrencyFormatting();
        ghsFormatting.setCurrency(ghs);
        ghsFormatting.setThousandsSeparator(",");
        ghsFormatting.setDecimalSeparator(".");
        ghsFormatting.setSymbolPosition("before");
        ghsFormatting.setSpaceAfterSymbol(false);
        ghs.setFormatting(ghsFormatting);

        CurrencyFxRules ghsFxRules = new CurrencyFxRules();
        ghsFxRules.setCurrency(ghs);
        ghsFxRules.setDefaultRateSource("BOG");
        ghsFxRules.setCrossBorderRestrictions(true);
        ghsFxRules.setMaxDailyLimit(10000.0);

        Set<AllowedCurrencyCross> ghsCrosses = Set.of(
                new AllowedCurrencyCross(ghsFxRules, "USD"),
                new AllowedCurrencyCross(ghsFxRules, "EUR"),
                new AllowedCurrencyCross(ghsFxRules, "NGN"),
                new AllowedCurrencyCross(ghsFxRules, "XOF")
        );
        ghsFxRules.setAllowedCrosses(ghsCrosses);
        ghs.setFxRules(ghsFxRules);

        currencyRepository.save(ghs);
    }

    private void initializeUSD() {
        Currency usd = new Currency();
        usd.setCode("USD");
        usd.setSymbol("$");
        usd.setName("US Dollar");
        usd.setPrecision(2);
        usd.setComplianceRef("global");
        usd.setStatus(CurrencyStatus.ACTIVE);
        usd.setPriority(5);
        usd.setRegion(Region.GLOBAL_RESERVE);

        CurrencyFormatting usdFormatting = new CurrencyFormatting();
        usdFormatting.setCurrency(usd);
        usdFormatting.setThousandsSeparator(",");
        usdFormatting.setDecimalSeparator(".");
        usdFormatting.setSymbolPosition("before");
        usdFormatting.setSpaceAfterSymbol(false);
        usd.setFormatting(usdFormatting);

        CurrencyFxRules usdFxRules = new CurrencyFxRules();
        usdFxRules.setCurrency(usd);
        usdFxRules.setDefaultRateSource("FED");
        usdFxRules.setCrossBorderRestrictions(false);
        usdFxRules.setMaxDailyLimit(null);

        Set<AllowedCurrencyCross> usdCrosses = Set.of(
                new AllowedCurrencyCross(usdFxRules, "ALL")
        );
        usdFxRules.setAllowedCrosses(usdCrosses);
        usd.setFxRules(usdFxRules);

        currencyRepository.save(usd);
    }

    private void initializeAdvancedFeatures() {
        AdvancedFeatures advancedFeatures = new AdvancedFeatures();
        advancedFeatures.setRealTimeConversion(true);
        advancedFeatures.setHistoricalRates(true);
        advancedFeatures.setMultiCurrencyWallets(true);
        advancedFeatures.setAutoCurrencySwitching(true);
        advancedFeatures.setOfflineMode(true);
        advancedFeatures.setRateAlerts(true);
        advancedFeatures.setPredictiveRates(false);
        advancedFeatures.setBatchProcessing(true);
        advancedFeatures.setApiRateLimits(1000);

        advancedFeaturesRepository.save(advancedFeatures);
    }

    private void initializeRegionalHierarchy() {
        // Get all currencies from repository
        Currency zar = currencyRepository.findByCurrencyCode("ZAR")
                .orElseThrow(() -> new RuntimeException("ZAR currency not found"));
        Currency ngn = currencyRepository.findByCurrencyCode("NGN")
                .orElseThrow(() -> new RuntimeException("NGN currency not found"));
        Currency kes = currencyRepository.findByCurrencyCode("KES")
                .orElseThrow(() -> new RuntimeException("KES currency not found"));
        Currency ghs = currencyRepository.findByCurrencyCode("GHS")
                .orElseThrow(() -> new RuntimeException("GHS currency not found"));
        Currency usd = currencyRepository.findByCurrencyCode("USD")
                .orElseThrow(() -> new RuntimeException("USD currency not found"));

        // Get other currencies or create them if they don't exist
        Currency eur = currencyRepository.findByCurrencyCode("EUR").orElseGet(() -> createCurrency("EUR", "Euro", "€", 2, Region.EUROPE, "ECB"));
        Currency gbp = currencyRepository.findByCurrencyCode("GBP").orElseGet(() -> createCurrency("GBP", "British Pound", "£", 2, Region.EUROPE, "BOE"));
        Currency cny = currencyRepository.findByCurrencyCode("CNY").orElseGet(() -> createCurrency("CNY", "Chinese Yuan", "¥", 2, Region.ASIA, "PBOC"));

        // Create additional African currencies
        Currency bwp = currencyRepository.findByCurrencyCode("BWP").orElseGet(() -> createCurrency("BWP", "Botswana Pula", "P", 2, Region.SOUTHERN_AFRICA, "BOB"));
        Currency mzn = currencyRepository.findByCurrencyCode("MZN").orElseGet(() -> createCurrency("MZN", "Mozambican Metical", "MT", 2, Region.SOUTHERN_AFRICA, "BM"));
        Currency zmw = currencyRepository.findByCurrencyCode("ZMW").orElseGet(() -> createCurrency("ZMW", "Zambian Kwacha", "ZK", 2, Region.SOUTHERN_AFRICA, "BOZ"));
        Currency mwk = currencyRepository.findByCurrencyCode("MWK").orElseGet(() -> createCurrency("MWK", "Malawian Kwacha", "MK", 2, Region.SOUTHERN_AFRICA, "RBM"));
        Currency nad = currencyRepository.findByCurrencyCode("NAD").orElseGet(() -> createCurrency("NAD", "Namibian Dollar", "N$", 2, Region.SOUTHERN_AFRICA, "BON"));

        Currency xof = currencyRepository.findByCurrencyCode("XOF").orElseGet(() -> createCurrency("XOF", "West African CFA Franc", "CFA", 0, Region.WEST_AFRICA, "BCEAO"));
        Currency xaf = currencyRepository.findByCurrencyCode("XAF").orElseGet(() -> createCurrency("XAF", "Central African CFA Franc", "FCFA", 0, Region.WEST_AFRICA, "BEAC"));
        Currency gnf = currencyRepository.findByCurrencyCode("GNF").orElseGet(() -> createCurrency("GNF", "Guinean Franc", "FG", 0, Region.WEST_AFRICA, "BCRG"));

        Currency tzs = currencyRepository.findByCurrencyCode("TZS").orElseGet(() -> createCurrency("TZS", "Tanzanian Shilling", "TSh", 2, Region.EAST_AFRICA, "BOT"));
        Currency rwf = currencyRepository.findByCurrencyCode("RWF").orElseGet(() -> createCurrency("RWF", "Rwandan Franc", "FRw", 0, Region.EAST_AFRICA, "BNR"));
        Currency etb = currencyRepository.findByCurrencyCode("ETB").orElseGet(() -> createCurrency("ETB", "Ethiopian Birr", "Br", 2, Region.EAST_AFRICA, "NBE"));

        Currency egp = currencyRepository.findByCurrencyCode("EGP").orElseGet(() -> createCurrency("EGP", "Egyptian Pound", "E£", 2, Region.NORTH_AFRICA, "CBE"));
        Currency mad = currencyRepository.findByCurrencyCode("MAD").orElseGet(() -> createCurrency("MAD", "Moroccan Dirham", "DH", 2, Region.NORTH_AFRICA, "BAM"));
        Currency dzd = currencyRepository.findByCurrencyCode("DZD").orElseGet(() -> createCurrency("DZD", "Algerian Dinar", "DA", 2, Region.NORTH_AFRICA, "BADR"));
        Currency tnd = currencyRepository.findByCurrencyCode("TND").orElseGet(() -> createCurrency("TND", "Tunisian Dinar", "DT", 3, Region.NORTH_AFRICA, "BCT"));

        // Emerging markets currencies
        Currency inr = currencyRepository.findByCurrencyCode("INR").orElseGet(() -> createCurrency("INR", "Indian Rupee", "₹", 2,Region.ASIA, "RBI"));
        Currency brl = currencyRepository.findByCurrencyCode("BRL").orElseGet(() -> createCurrency("BRL", "Brazilian Real", "R$", 2, Region.SOUTHERN_AMERICA, "BCB"));
        Currency trl = currencyRepository.findByCurrencyCode("TRY").orElseGet(() -> createCurrency("TRY", "Turkish Lira", "₺", 2, Region.EUROPE, "CBRT"));
        Currency mxn = currencyRepository.findByCurrencyCode("MXN").orElseGet(() -> createCurrency("MXN", "Mexican Peso", "$", 2, Region.NOTHERN_AMERICA, "BdM"));

        // Southern Africa Regional Hierarchy
        RegionalHierarchy southernAfrica = new RegionalHierarchy();
        southernAfrica.setRegionName("southernAfrica");
        southernAfrica.setCurrencies(Set.of(zar, bwp, mzn, zmw, mwk, nad));
        regionalHierarchyRepository.save(southernAfrica);

        // West Africa Regional Hierarchy
        RegionalHierarchy westAfrica = new RegionalHierarchy();
        westAfrica.setRegionName("westAfrica");
        westAfrica.setCurrencies(Set.of(ngn, ghs, xof, xaf, gnf));
        regionalHierarchyRepository.save(westAfrica);

        // East Africa Regional Hierarchy
        RegionalHierarchy eastAfrica = new RegionalHierarchy();
        eastAfrica.setRegionName("eastAfrica");
        //eastAfrica.setCurrencies(Set.of(kes, tzs, ugx, rwf, etb));
        eastAfrica.setCurrencies(Set.of(kes, tzs, rwf, etb));
        regionalHierarchyRepository.save(eastAfrica);

        // North Africa Regional Hierarchy
        RegionalHierarchy northAfrica = new RegionalHierarchy();
        northAfrica.setRegionName("northAfrica");
        northAfrica.setCurrencies(Set.of(egp, mad, dzd, tnd));
        regionalHierarchyRepository.save(northAfrica);

        // Global Reserve Regional Hierarchy
        RegionalHierarchy globalReserve = new RegionalHierarchy();
        globalReserve.setRegionName("globalReserve");
        globalReserve.setCurrencies(Set.of(usd, eur, gbp, cny));
        regionalHierarchyRepository.save(globalReserve);

        // Emerging Markets Regional Hierarchy
        RegionalHierarchy emergingMarkets = new RegionalHierarchy();
        emergingMarkets.setRegionName("emergingMarkets");
        emergingMarkets.setCurrencies(Set.of(inr, brl, trl, mxn));
        regionalHierarchyRepository.save(emergingMarkets);
    }

    private Currency createCurrency(String code, String name, String symbol, int precision, Region region, String centralBank) {
        Currency currency = new Currency();
        currency.setCode(code);
        currency.setName(name);
        currency.setSymbol(symbol);
        currency.setPrecision(precision);
        currency.setRegion(region);
        currency.setCentralBank(centralBank);
        currency.setStatus(CurrencyStatus.ACTIVE);
        currency.setPriority(99); // Default priority for auto-created currencies

        // Create basic formatting
        CurrencyFormatting formatting = new CurrencyFormatting();
        formatting.setCurrency(currency);
        formatting.setThousandsSeparator(",");
        formatting.setDecimalSeparator(".");
        formatting.setSymbolPosition("before");
        formatting.setSpaceAfterSymbol(false);
        currency.setFormatting(formatting);

        // Create basic FX rules
        CurrencyFxRules fxRules = new CurrencyFxRules();
        fxRules.setCurrency(currency);
        fxRules.setDefaultRateSource(centralBank);
        fxRules.setCrossBorderRestrictions(true);
        fxRules.setMaxDailyLimit(10000.0);
        currency.setFxRules(fxRules);

        return currencyRepository.save(currency);
    }
}