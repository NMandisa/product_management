package za.co.pms.data;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import za.co.pms.enums.RegionCode;
import za.co.pms.exception.ValidationException;
import za.co.pms.model.settings.LocalizationEngine;
import za.co.pms.model.settings.localization.*;
import za.co.pms.repository.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author NMMkhungo
 * @since 2025/09/28
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class LocalizationDataInitializer {

    // Group related repositories
    private final LanguageRepository languageRepository;
    private final RegionalSettingsRepository regionalSettingsRepository;
    private final LocalizationConfigRepository localizationConfigRepository;
    private final CulturalAdaptationsRepository culturalAdaptationsRepository;
    private final DateFormatConfigRepository dateFormatConfigRepository;
    private final TimeFormatConfigRepository timeFormatConfigRepository;
    private final NumberFormatConfigRepository numberFormatConfigRepository;

    @PostConstruct
    public void initialize() {
        try {
            if (shouldInitialize()) {
                log.info("Starting localization data initialization...");
                initializeData();
                validateInitialization();
                log.info("Localization data initialization completed successfully");
            } else {
                log.info("Localization data already exists, skipping initialization");
            }
        } catch (Exception e) {
            log.error("Localization data initialization failed", e);
            // Don't throw exception to allow application to start
            // In production, you might want to use a circuit breaker pattern here
        }
    }

    private boolean shouldInitialize() {
        return localizationConfigRepository.count() == 0 &&
                languageRepository.count() == 0;
    }

    @Transactional
    protected void initializeData() {
        initializeLanguages();
        initializeCulturalAdaptations();
        initializeRegionalSettings();
        initializeLocalizationConfig();
    }

    private void validateInitialization() {
        // Validate that essential languages are created
        List<String> requiredLanguages = Arrays.asList("en", "zu", "af", "fr", "sw", "ar");
        for (String langCode : requiredLanguages) {
            languageRepository.findById(langCode)
                    .orElseThrow(() -> new ValidationException("Required language not created: " + langCode));
        }

        // Validate regional settings
        List<String> requiredRegions = Arrays.asList("southernAfrica", "westAfrica", "eastAfrica", "northAfrica");
        for (String region : requiredRegions) {
            regionalSettingsRepository.findByRegionGroup(region)
                    .orElseThrow(() -> new ValidationException("Required regional settings not created: " + region));
        }

        log.info("Localization data validation passed");
    }

    private void initializeLanguages() {
        log.info("Initializing languages...");

        List<Language> languages = Arrays.asList(
                // English (default)
                createLanguage("en", "English", "English", false, true, Set.of()),

                // Southern African languages
                createLanguage("zu", "Zulu", "isiZulu", false, false, Set.of(RegionCode.ZA)),
                createLanguage("af", "Afrikaans", "Afrikaans", false, false, Set.of(RegionCode.ZA)),
                createLanguage("st", "Sotho", "Sesotho", false, false, Set.of(RegionCode.ZA, RegionCode.LS)),
                createLanguage("tn", "Tswana", "Setswana", false, false, Set.of(RegionCode.ZA, RegionCode.BW)),
                createLanguage("ts", "Tsonga", "Xitsonga", false, false, Set.of(RegionCode.ZA, RegionCode.MZ)),
                createLanguage("ve", "Venda", "Tshivenda", false, false, Set.of(RegionCode.ZA)),
                createLanguage("ss", "Swati", "SiSwati", false, false, Set.of(RegionCode.ZA, RegionCode.SZ)),
                createLanguage("nr", "Ndebele", "isiNdebele", false, false, Set.of(RegionCode.ZA)),
                createLanguage("xh", "Xhosa", "isiXhosa", false, false, Set.of(RegionCode.ZA)),

                // Portuguese for Mozambique
                createLanguage("pt", "Portuguese", "Português", false, false, Set.of(RegionCode.MZ)),

                // West African languages
                createLanguage("fr", "French", "Français", false, false,
                        Set.of(RegionCode.SN, RegionCode.CI, RegionCode.BF, RegionCode.BJ, RegionCode.NE, RegionCode.TG)),
                createLanguage("ha", "Hausa", "Hausa", false, false,
                        Set.of(RegionCode.NG, RegionCode.NE, RegionCode.GH)),
                createLanguage("yo", "Yoruba", "Yorùbá", false, false,
                        Set.of(RegionCode.NG, RegionCode.BJ)),
                createLanguage("ig", "Igbo", "Igbo", false, false, Set.of(RegionCode.NG)),
                createLanguage("ff", "Fulani", "Fulfulde", false, false,
                        Set.of(RegionCode.NG, RegionCode.SN, RegionCode.GN)),
                createLanguage("wo", "Wolof", "Wolof", false, false,
                        Set.of(RegionCode.SN, RegionCode.GM)),

                // East African languages
                createLanguage("sw", "Swahili", "Kiswahili", false, false,
                        Set.of(RegionCode.KE, RegionCode.TZ, RegionCode.UG, RegionCode.RW, RegionCode.BI)),
                createLanguage("am", "Amharic", "አማርኛ", false, false, Set.of(RegionCode.ET)),
                createLanguage("om", "Oromo", "Afaan Oromoo", false, false, Set.of(RegionCode.ET)),
                createLanguage("so", "Somali", "Soomaali", false, false,
                        Set.of(RegionCode.SO, RegionCode.ET, RegionCode.KE)),
                createLanguage("rw", "Kinyarwanda", "Kinyarwanda", false, false, Set.of(RegionCode.RW)),

                // North African languages
                createLanguage("ar", "Arabic", "العربية", true, false,
                        Set.of(RegionCode.EG, RegionCode.MA, RegionCode.DZ, RegionCode.TN, RegionCode.LY)),
                createLanguage("ber", "Berber", "Tamaziɣt", false, false,
                        Set.of(RegionCode.MA, RegionCode.DZ)),

                // Global languages
                createLanguage("zh", "Chinese", "中文", false, false, Set.of(RegionCode.CN)),
                createLanguage("hi", "Hindi", "हिन्दी", false, false, Set.of(RegionCode.IN)),
                createLanguage("ja", "Japanese", "日本語", false, false, Set.of(RegionCode.JP)),
                createLanguage("de", "German", "Deutsch", false, false, Set.of(RegionCode.DE)),
                createLanguage("es", "Spanish", "Español", false, false, Set.of(RegionCode.ES))
        );

        // Batch save for better performance
        List<Language> savedLanguages = languageRepository.saveAll(languages);
        log.info("Initialized {} languages", savedLanguages.size());
    }

    private Language createLanguage(String code, String name, String nativeName,
                                    boolean rtl, boolean isDefault, Set<RegionCode> regions) {
        Language language = new Language(code, name, nativeName, rtl, isDefault);

        if (!regions.isEmpty()) {
            Set<LanguageRegion> languageRegions = regions.stream()
                    .map(region -> new LanguageRegion(language, region))
                    .collect(Collectors.toSet());
            language.setLanguageRegions(languageRegions);
        }

        return language;
    }

    private void initializeCulturalAdaptations() {
        log.info("Initializing cultural adaptations...");

        CulturalAdaptations culturalAdaptations = new CulturalAdaptations();
        culturalAdaptations.setLocalHolidays(true);
        /*culturalAdaptations.setCulturalSensitivity(true);
        culturalAdaptations.setReligiousObservances(true);*/

        // Initialize calendars
        Set<SupportedCalendar> calendars = new HashSet<>(Arrays.asList(
                new SupportedCalendar("gregorian", culturalAdaptations),
                new SupportedCalendar("islamic", culturalAdaptations),
                new SupportedCalendar("ethiopian", culturalAdaptations),
                new SupportedCalendar("coptic", culturalAdaptations)
        ));
        culturalAdaptations.setSupportedCalendars(calendars);

        // Initialize number systems
        Set<SupportedNumberSystem> numberSystems = new HashSet<>(Arrays.asList(
                new SupportedNumberSystem("latin", culturalAdaptations),
                new SupportedNumberSystem("arabic", culturalAdaptations),
                new SupportedNumberSystem("devanagari", culturalAdaptations),
                new SupportedNumberSystem("bengali", culturalAdaptations)
        ));
        culturalAdaptations.setSupportedNumberSystems(numberSystems);

        // Initialize currency placement
        CurrencyPlacementConfig currencyPlacement = new CurrencyPlacementConfig();
        currencyPlacement.setDefaultPlacement("before");
        currencyPlacement.setCulturalAdaptations(culturalAdaptations);

        Set<CurrencyPlacementException> placementExceptions = new HashSet<>(Arrays.asList(
                new CurrencyPlacementException("fr", "after", currencyPlacement),
                new CurrencyPlacementException("ar", "beforeNoSpace", currencyPlacement),
                new CurrencyPlacementException("ja", "beforeNoSpace", currencyPlacement),
                new CurrencyPlacementException("de", "afterSpace", currencyPlacement)
        ));
        currencyPlacement.setExceptions(placementExceptions);
        culturalAdaptations.setCurrencyPlacement(currencyPlacement);

        CulturalAdaptations savedAdaptations = culturalAdaptationsRepository.save(culturalAdaptations);

        // Initialize formats
        initializeFormats(savedAdaptations);

        log.info("Cultural adaptations initialized");
    }

    private void initializeFormats(CulturalAdaptations culturalAdaptations) {
        // Date formats
        List<DateFormatConfig> dateFormats = Arrays.asList(
                new DateFormatConfig(RegionCode.ZA, "yyyy/MM/dd", culturalAdaptations),
                new DateFormatConfig(RegionCode.NG, "dd/MM/yyyy", culturalAdaptations),
                new DateFormatConfig(RegionCode.KE, "dd-MM-yyyy", culturalAdaptations),
                new DateFormatConfig(RegionCode.ET, "dd/MM/yyyy", culturalAdaptations),
                new DateFormatConfig(RegionCode.EG, "dd/MM/yyyy", culturalAdaptations),
                new DateFormatConfig(RegionCode.MA, "dd/MM/yyyy", culturalAdaptations),
                new DateFormatConfig(RegionCode.SN, "dd/MM/yyyy", culturalAdaptations),
                new DateFormatConfig(RegionCode.CN, "yyyy-MM-dd", culturalAdaptations),
                new DateFormatConfig(RegionCode.US, "MM/dd/yyyy", culturalAdaptations),
                new DateFormatConfig(null, "yyyy-MM-dd", culturalAdaptations)
        );
        dateFormatConfigRepository.saveAll(dateFormats);

        // Time formats - FIXED: Use RegionCode enum instead of String for regionCode
        List<TimeFormatConfig> timeFormats = Arrays.asList(
                new TimeFormatConfig(RegionCode.ZA, "HH:mm", culturalAdaptations),
                new TimeFormatConfig(RegionCode.NG, "hh:mm a", culturalAdaptations),
                new TimeFormatConfig(RegionCode.KE, "HH:mm", culturalAdaptations),
                new TimeFormatConfig(RegionCode.ET, "h:mm a", culturalAdaptations),
                new TimeFormatConfig(RegionCode.EG, "HH:mm", culturalAdaptations),
                new TimeFormatConfig(RegionCode.US, "h:mm a", culturalAdaptations),
                new TimeFormatConfig(null, "HH:mm", culturalAdaptations)
        );
        timeFormatConfigRepository.saveAll(timeFormats);

        // Number formats - FIXED: Use RegionCode enum instead of String for regionCode
        List<NumberFormatConfig> numberFormats = Arrays.asList(
                new NumberFormatConfig(RegionCode.ZA, ".", " ", "[3,3]", culturalAdaptations),
                new NumberFormatConfig(RegionCode.NG, ".", ",", "[3,3]", culturalAdaptations),
                new NumberFormatConfig(RegionCode.KE, ".", ",", "[3,3]", culturalAdaptations),
                new NumberFormatConfig(RegionCode.FR, ",", " ", "[3,3]", culturalAdaptations),
                new NumberFormatConfig(RegionCode.DE, ",", ".", "[3,3]", culturalAdaptations),
                new NumberFormatConfig(RegionCode.IN, ".", ",", "[3,2]", culturalAdaptations),
                new NumberFormatConfig(null, ".", ",", "[3,3]", culturalAdaptations)
        );
        numberFormatConfigRepository.saveAll(numberFormats);
    }

    private void initializeRegionalSettings() {
        log.info("Initializing regional settings...");

        List<RegionalSettings> regionalSettings = Arrays.asList(
                createSouthernAfricaSettings(),
                createWestAfricaSettings(),
                createEastAfricaSettings(),
                createNorthAfricaSettings(),
                createGlobalSettings()
        );

        regionalSettingsRepository.saveAll(regionalSettings);
        log.info("Regional settings initialized for {} regions", regionalSettings.size());
    }

    private RegionalSettings createSouthernAfricaSettings() {
        RegionalSettings settings = new RegionalSettings();
        settings.setRegionGroup("southernAfrica");
        settings.setDefaultLanguage("en");
        settings.setDateFormat("yyyy/MM/dd");
        settings.setTimeFormat("HH:mm");
        settings.setFirstDayOfWeek(0); // Sunday

        Set<RegionalFallbackLanguage> fallbacks = new HashSet<>(Arrays.asList(
                new RegionalFallbackLanguage("zu", 1, settings),
                new RegionalFallbackLanguage("af", 2, settings),
                new RegionalFallbackLanguage("st", 3, settings)
        ));
        settings.setFallbackLanguages(fallbacks);

        return settings;
    }

    private RegionalSettings createWestAfricaSettings() {
        RegionalSettings settings = new RegionalSettings();
        settings.setRegionGroup("westAfrica");
        settings.setDefaultLanguage("en");
        settings.setDateFormat("dd/MM/yyyy");
        settings.setTimeFormat("hh:mm a");
        settings.setFirstDayOfWeek(1); // Monday

        Set<RegionalFallbackLanguage> fallbacks = new HashSet<>(Arrays.asList(
                new RegionalFallbackLanguage("fr", 1, settings),
                new RegionalFallbackLanguage("ha", 2, settings),
                new RegionalFallbackLanguage("yo", 3, settings),
                new RegionalFallbackLanguage("ff", 4, settings)
        ));
        settings.setFallbackLanguages(fallbacks);

        return settings;
    }

    private RegionalSettings createEastAfricaSettings() {
        RegionalSettings settings = new RegionalSettings();
        settings.setRegionGroup("eastAfrica");
        settings.setDefaultLanguage("sw");
        settings.setDateFormat("dd-MM-yyyy");
        settings.setTimeFormat("HH:mm");
        settings.setFirstDayOfWeek(0); // Sunday

        Set<RegionalFallbackLanguage> fallbacks = new HashSet<>(Arrays.asList(
                new RegionalFallbackLanguage("en", 1, settings),
                new RegionalFallbackLanguage("am", 2, settings),
                new RegionalFallbackLanguage("so", 3, settings)
        ));
        settings.setFallbackLanguages(fallbacks);

        return settings;
    }

    private RegionalSettings createNorthAfricaSettings() {
        RegionalSettings settings = new RegionalSettings();
        settings.setRegionGroup("northAfrica");
        settings.setDefaultLanguage("ar");
        settings.setDateFormat("dd/MM/yyyy");
        settings.setTimeFormat("HH:mm");
        settings.setFirstDayOfWeek(6); // Saturday

        Set<RegionalFallbackLanguage> fallbacks = new HashSet<>(Arrays.asList(
                new RegionalFallbackLanguage("fr", 1, settings),
                new RegionalFallbackLanguage("ber", 2, settings),
                new RegionalFallbackLanguage("en", 3, settings)
        ));
        settings.setFallbackLanguages(fallbacks);

        return settings;
    }

    private RegionalSettings createGlobalSettings() {
        RegionalSettings settings = new RegionalSettings();
        settings.setRegionGroup("global");
        settings.setDefaultLanguage("en");
        settings.setDateFormat("yyyy-MM-dd");
        settings.setTimeFormat("HH:mm");
        settings.setFirstDayOfWeek(1); // Monday

        Set<RegionalFallbackLanguage> fallbacks = new HashSet<>(Arrays.asList(
                new RegionalFallbackLanguage("es", 1, settings),
                new RegionalFallbackLanguage("fr", 2, settings),
                new RegionalFallbackLanguage("zh", 3, settings)
        ));
        settings.setFallbackLanguages(fallbacks);

        return settings;
    }

    private void initializeLocalizationConfig() {
        log.info("Initializing localization configuration...");

        CulturalAdaptations culturalAdaptations = culturalAdaptationsRepository.findAll().get(0);

        LocalizationConfig config = new LocalizationConfig();
        config.setRtlSupport(true);
        config.setLocaleAwareFormatting(true);
        config.addCulturalAdaptations(culturalAdaptations);
        config.setAccessibility(initializeAccessibility());
        config.setLocalizationEngine(initializeLocalizationEngine());

        localizationConfigRepository.save(config);
        log.info("Localization configuration initialized");
    }

    private AccessibilitySettings initializeAccessibility() {
        AccessibilitySettings accessibility = new AccessibilitySettings();
        accessibility.setHighContrast(true);
        accessibility.setLargeText(true);
        accessibility.setScreenReader(true);
        accessibility.setVoiceCommands(false);
        return accessibility;
    }

    private LocalizationEngine initializeLocalizationEngine() {
        LocalizationEngine engine = new LocalizationEngine();
        engine.setAutoDetection(true);
        engine.setFallbackChains(true);
        engine.setContextAware(true);
        engine.setVersion("2.0.0");
        return engine;
    }
}
