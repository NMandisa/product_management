package za.co.pms.data;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import za.co.pms.model.settings.LocalizationEngine;
import za.co.pms.model.settings.localization.*;
import za.co.pms.repository.*;

import java.util.HashSet;
import java.util.Set;

/**
 * @author NMMkhungo
 * @since 2025/09/28
 **/
@Slf4j
@Component
public class LocalizationDataInitializer {

    private final LanguageRepository languageRepository;
    private final RegionalSettingsRepository regionalSettingsRepository;
    private final LocalizationConfigRepository localizationConfigRepository;
    private final CulturalAdaptationsRepository culturalAdaptationsRepository;
    private final DateFormatConfigRepository dateFormatConfigRepository;
    private final TimeFormatConfigRepository timeFormatConfigRepository;
    private final NumberFormatConfigRepository numberFormatConfigRepository;

    public LocalizationDataInitializer(LanguageRepository languageRepository,
                                       RegionalSettingsRepository regionalSettingsRepository,
                                       LocalizationConfigRepository localizationConfigRepository,
                                       CulturalAdaptationsRepository culturalAdaptationsRepository,
                                       DateFormatConfigRepository dateFormatConfigRepository,
                                       TimeFormatConfigRepository timeFormatConfigRepository,
                                       NumberFormatConfigRepository numberFormatConfigRepository){
    this.localizationConfigRepository=localizationConfigRepository;
    this.culturalAdaptationsRepository=culturalAdaptationsRepository;
    this.dateFormatConfigRepository=dateFormatConfigRepository;
    this.numberFormatConfigRepository=numberFormatConfigRepository;
    this.regionalSettingsRepository=regionalSettingsRepository;
    this.languageRepository=languageRepository;
    this.timeFormatConfigRepository=timeFormatConfigRepository;
    }

    @PostConstruct
    @Transactional
    public void initialize() {
        if (localizationConfigRepository.count() == 0) {
            initializeLanguages();
            initializeCulturalAdaptations();
            initializeRegionalSettings();
            initializeLocalizationConfig();
            initializeAccessibility();
            initializeLocalizationEngine();
        }
    }

    private void initializeLanguages() {
        // English (default)
        Language en = new Language("en", "English", "English", false, true);
        en.setLanguageRegions(new HashSet<>()); // English is global, no specific region
        languageRepository.save(en);

        // Zulu
        Language zu = new Language("zu", "Zulu", "isiZulu", false, false);
        zu.setLanguageRegions(Set.of(new LanguageRegion(zu, "ZA")));
        languageRepository.save(zu);

        // Afrikaans
        Language af = new Language("af", "Afrikaans", "Afrikaans", false, false);
        af.setLanguageRegions(Set.of(new LanguageRegion(af, "ZA")));
        languageRepository.save(af);

        // Portuguese
        Language pt = new Language("pt", "Portuguese", "Português", false, false);
        pt.setLanguageRegions(Set.of(new LanguageRegion(pt, "MZ")));
        languageRepository.save(pt);

        // French
        Language fr = new Language("fr", "French", "Français", false, false);
        fr.setLanguageRegions(Set.of(
                new LanguageRegion(fr, "SN"),
                new LanguageRegion(fr, "CI")
        ));
        languageRepository.save(fr);

        // Swahili
        Language sw = new Language("sw", "Swahili", "Kiswahili", false, false);
        sw.setLanguageRegions(Set.of(
                new LanguageRegion(sw, "KE"),
                new LanguageRegion(sw, "TZ"),
                new LanguageRegion(sw, "UG")
        ));
        languageRepository.save(sw);

        // Arabic
        Language ar = new Language("ar", "Arabic", "العربية", true, false);
        ar.setLanguageRegions(Set.of(new LanguageRegion(ar, "EG")));
        languageRepository.save(ar);

        // Hausa
        Language ha = new Language("ha", "Hausa", "Hausa", false, false);
        ha.setLanguageRegions(Set.of(new LanguageRegion(ha, "NG")));
        languageRepository.save(ha);

        // Yoruba
        Language yo = new Language("yo", "Yoruba", "Yorùbá", false, false);
        yo.setLanguageRegions(Set.of(new LanguageRegion(yo, "NG")));
        languageRepository.save(yo);

        // Chinese
        Language zh = new Language("zh", "Chinese", "中文", false, false);
        zh.setLanguageRegions(Set.of(new LanguageRegion(zh, "CN")));
        languageRepository.save(zh);

        // Spanish
        Language es = new Language("es", "Spanish", "Español", false, false);
        es.setLanguageRegions(Set.of(new LanguageRegion(es, "GLOBAL")));
        languageRepository.save(es);

        // Hindi
        Language hi = new Language("hi", "Hindi", "हिन्दी", false, false);
        hi.setLanguageRegions(Set.of(new LanguageRegion(hi, "IN")));
        languageRepository.save(hi);

        // Japanese
        Language ja = new Language("ja", "Japanese", "日本語", false, false);
        ja.setLanguageRegions(Set.of(new LanguageRegion(ja, "JP")));
        languageRepository.save(ja);
    }

    private void initializeCulturalAdaptations() {
        CulturalAdaptations culturalAdaptations = new CulturalAdaptations();
        culturalAdaptations.setLocalHolidays(true);

        // Initialize calendars
        Set<SupportedCalendar> calendars = Set.of(
                new SupportedCalendar("gregorian", culturalAdaptations),
                new SupportedCalendar("islamic", culturalAdaptations),
                new SupportedCalendar("ethiopian", culturalAdaptations)
        );
        culturalAdaptations.setSupportedCalendars(calendars);

        // Initialize number systems
        Set<SupportedNumberSystem> numberSystems = Set.of(
                new SupportedNumberSystem("latin", culturalAdaptations),
                new SupportedNumberSystem("arabic", culturalAdaptations),
                new SupportedNumberSystem("devanagari", culturalAdaptations)
        );
        culturalAdaptations.setSupportedNumberSystems(numberSystems);

        // Initialize currency placement
        CurrencyPlacementConfig currencyPlacement = new CurrencyPlacementConfig();
        currencyPlacement.setDefaultPlacement("before");
        currencyPlacement.setCulturalAdaptations(culturalAdaptations);

        Set<CurrencyPlacementException> placementExceptions = Set.of(
                new CurrencyPlacementException("fr", "after", currencyPlacement),
                new CurrencyPlacementException("ar", "beforeNoSpace", currencyPlacement),
                new CurrencyPlacementException("ja", "beforeNoSpace", currencyPlacement)
        );
        currencyPlacement.setExceptions(placementExceptions);
        culturalAdaptations.setCurrencyPlacement(currencyPlacement);

        culturalAdaptationsRepository.save(culturalAdaptations);

        // Initialize date formats
        initializeDateFormats(culturalAdaptations);
        initializeTimeFormats(culturalAdaptations);
        initializeNumberFormats(culturalAdaptations);
    }

    private void initializeDateFormats(CulturalAdaptations culturalAdaptations) {
        Set<DateFormatConfig> dateFormats = Set.of(
                new DateFormatConfig("ZA", "yyyy/MM/dd", culturalAdaptations),
                new DateFormatConfig("NG", "dd/MM/yyyy", culturalAdaptations),
                new DateFormatConfig("KE", "dd-MM-yyyy", culturalAdaptations),
                new DateFormatConfig("EG", "dd/MM/yyyy", culturalAdaptations),
                new DateFormatConfig("CN", "yyyy-MM-dd", culturalAdaptations),
                new DateFormatConfig(null, "yyyy-MM-dd", culturalAdaptations) // default
        );
        dateFormatConfigRepository.saveAll(dateFormats);
    }

    private void initializeTimeFormats(CulturalAdaptations culturalAdaptations) {
        Set<TimeFormatConfig> timeFormats = Set.of(
                new TimeFormatConfig("ZA", "HH:mm", culturalAdaptations),
                new TimeFormatConfig("NG", "hh:mm a", culturalAdaptations),
                new TimeFormatConfig("KE", "HH:mm", culturalAdaptations),
                new TimeFormatConfig(null, "HH:mm", culturalAdaptations) // default
        );
        timeFormatConfigRepository.saveAll(timeFormats);
    }

    private void initializeNumberFormats(CulturalAdaptations culturalAdaptations) {
        Set<NumberFormatConfig> numberFormats = Set.of(
                new NumberFormatConfig("ZA", ".", " ", "[3,3]", culturalAdaptations),
                new NumberFormatConfig("NG", ".", ",", "[3,3]", culturalAdaptations),
                new NumberFormatConfig("FR", ",", " ", "[3,3]", culturalAdaptations),
                new NumberFormatConfig(null, ".", ",", "[3,3]", culturalAdaptations) // default
        );
        numberFormatConfigRepository.saveAll(numberFormats);
    }

    private void initializeRegionalSettings() {
        // Southern Africa
        RegionalSettings southernAfrica = new RegionalSettings();
        southernAfrica.setRegionGroup("southernAfrica");
        southernAfrica.setDefaultLanguage("en");
        southernAfrica.setDateFormat("yyyy/MM/dd");
        southernAfrica.setTimeFormat("HH:mm");
        southernAfrica.setFirstDayOfWeek(0);

        Set<RegionalFallbackLanguage> southernFallbacks = Set.of(
                new RegionalFallbackLanguage("zu", 1, southernAfrica),
                new RegionalFallbackLanguage("af", 2, southernAfrica)
        );
        southernAfrica.setFallbackLanguages(southernFallbacks);
        regionalSettingsRepository.save(southernAfrica);

        // West Africa
        RegionalSettings westAfrica = new RegionalSettings();
        westAfrica.setRegionGroup("westAfrica");
        westAfrica.setDefaultLanguage("en");
        westAfrica.setDateFormat("dd/MM/yyyy");
        westAfrica.setTimeFormat("hh:mm a");
        westAfrica.setFirstDayOfWeek(1);

        Set<RegionalFallbackLanguage> westFallbacks = Set.of(
                new RegionalFallbackLanguage("fr", 1, westAfrica),
                new RegionalFallbackLanguage("ha", 2, westAfrica),
                new RegionalFallbackLanguage("yo", 3, westAfrica)
        );
        westAfrica.setFallbackLanguages(westFallbacks);
        regionalSettingsRepository.save(westAfrica);

        // East Africa
        RegionalSettings eastAfrica = new RegionalSettings();
        eastAfrica.setRegionGroup("eastAfrica");
        eastAfrica.setDefaultLanguage("sw");
        eastAfrica.setDateFormat("dd-MM-yyyy");
        eastAfrica.setTimeFormat("HH:mm");
        eastAfrica.setFirstDayOfWeek(0);

        Set<RegionalFallbackLanguage> eastFallbacks = Set.of(
                new RegionalFallbackLanguage("en", 1, eastAfrica),
                new RegionalFallbackLanguage("ar", 2, eastAfrica)
        );
        eastAfrica.setFallbackLanguages(eastFallbacks);
        regionalSettingsRepository.save(eastAfrica);

        // North Africa
        RegionalSettings northAfrica = new RegionalSettings();
        northAfrica.setRegionGroup("northAfrica");
        northAfrica.setDefaultLanguage("ar");
        northAfrica.setDateFormat("dd/MM/yyyy");
        northAfrica.setTimeFormat("HH:mm");
        northAfrica.setFirstDayOfWeek(6);

        Set<RegionalFallbackLanguage> northFallbacks = Set.of(
                new RegionalFallbackLanguage("fr", 1, northAfrica),
                new RegionalFallbackLanguage("en", 2, northAfrica)
        );
        northAfrica.setFallbackLanguages(northFallbacks);
        regionalSettingsRepository.save(northAfrica);
    }

    private void initializeLocalizationConfig() {
        CulturalAdaptations culturalAdaptations = culturalAdaptationsRepository.findAll().get(0);
        AccessibilitySettings accessibility = initializeAccessibility();
        LocalizationEngine engine = initializeLocalizationEngine();

        LocalizationConfig config = new LocalizationConfig();
        config.setRtlSupport(true);
        config.setLocaleAwareFormatting(true);
        //config.setCulturalAdaptations(culturalAdaptations);
        config.addCulturalAdaptations(culturalAdaptations);
        config.setAccessibility(accessibility);
        config.setLocalizationEngine(engine);

        localizationConfigRepository.save(config);
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
        engine.setVersion("2025-09-26");
        return engine;
    }
}
