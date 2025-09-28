package za.co.pms.util;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import za.co.pms.model.settings.compliance.ComplianceFlag;
import za.co.pms.model.settings.compliance.ComplianceRegion;
import za.co.pms.model.settings.compliance.RequiredLicense;
import za.co.pms.repository.ComplianceEngineRepository;
import za.co.pms.repository.ComplianceRegionRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author NMMkhungo
 * @since 2025/09/28
 **/
@Slf4j
@Component
@Transactional
@RequiredArgsConstructor
public class ComplianceUtil {

    private ComplianceRegionRepository complianceRegionRepository;
    private ComplianceEngineRepository complianceEngineRepository;

    public ComplianceRegion getComplianceRegion(String regionCode) {
        return complianceRegionRepository.findByRegionCodeWithAdditionalRules(regionCode)
                .orElseThrow(() -> new RuntimeException("Compliance region not found: " + regionCode));
    }

    public Map<String, Object> getComplianceConfiguration(String regionCode) {
        ComplianceRegion region = getComplianceRegion(regionCode);
        Map<String, Object> config = new HashMap<>();

        // Build configuration map similar to original JSON structure
        config.put("taxAuthority", region.getTaxAuthority());
        config.put("VAT", region.getVatRate());
        config.put("withholdingTax", region.getWithholdingTaxRate());
        config.put("reportingThreshold", region.getReportingThreshold());

        // Add compliance flags
        Map<String, Boolean> flags = region.getComplianceFlags().stream()
                .collect(Collectors.toMap(ComplianceFlag::getName, ComplianceFlag::getValue));
        config.putAll(flags);

        // Add licenses
        List<String> licenses = region.getRequiredLicenses().stream()
                .map(RequiredLicense::getName)
                .collect(Collectors.toList());
        config.put("requiredLicenses", licenses);

        // Add additional rules
        if (region.getAdditionalRules() != null) {
            Map<String, Object> additionalRules = new HashMap<>();

            region.getAdditionalRules().getRuleFlags().forEach(flag ->
                    additionalRules.put(flag.getRuleName(), flag.getRuleValue()));

            region.getAdditionalRules().getRuleThresholds().forEach(threshold ->
                    additionalRules.put(threshold.getThresholdName(), threshold.getThresholdValue()));

            config.put("additionalRules", additionalRules);
        }

        return config;
    }
}