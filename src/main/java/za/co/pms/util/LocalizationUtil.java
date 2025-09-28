package za.co.pms.util;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import za.co.pms.model.settings.localization.DateFormatConfig;
import za.co.pms.model.settings.localization.Language;
import za.co.pms.model.settings.localization.RegionalSettings;
import za.co.pms.repository.DateFormatConfigRepository;
import za.co.pms.repository.LanguageRepository;
import za.co.pms.repository.RegionalSettingsRepository;

import java.util.List;

/**
 * @author NMMkhungo
 * @since 2025/09/28
 **/
@Slf4j
@Component
@Transactional
public class LocalizationUtil {

    private final LanguageRepository languageRepository;
    private final RegionalSettingsRepository regionalSettingsRepository;
    private final DateFormatConfigRepository dateFormatConfigRepository;

    public  LocalizationUtil(LanguageRepository languageRepository,
                             RegionalSettingsRepository regionalSettingsRepository,
                             DateFormatConfigRepository dateFormatConfigRepository){
        this.languageRepository =languageRepository;
        this.regionalSettingsRepository = regionalSettingsRepository;
        this.dateFormatConfigRepository = dateFormatConfigRepository;

    }

    public Language getDefaultLanguage() {
        return languageRepository.findDefaultLanguage()
                .orElseThrow(() -> new RuntimeException("Default language not found"));
    }

    public List<Language> getLanguagesByRegion(String regionCode) {
        return languageRepository.findByRegionCode(regionCode);
    }

    public String getDateFormatForRegion(String regionCode) {
        List<DateFormatConfig> formats = dateFormatConfigRepository.findDateFormatByRegion(regionCode);
        return formats.stream()
                .filter(df -> regionCode.equals(df.getRegionCode()))
                .findFirst()
                .orElse(formats.stream()
                        .filter(df -> df.getRegionCode() == null)
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("No date format found")))
                .getDateFormat();
    }

    public RegionalSettings getRegionalSettings(String regionGroup) {
        return regionalSettingsRepository.findByRegionGroupWithFallbacks(regionGroup)
                .orElseThrow(() -> new RuntimeException("Regional settings not found: " + regionGroup));
    }
}
