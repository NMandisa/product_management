package za.co.pms.data;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;
import za.co.pms.model.settings.compliance.ComplianceFlag;
import za.co.pms.model.settings.compliance.ComplianceRegion;
import za.co.pms.repository.ComplianceRegionRepository;

import java.util.HashSet;
import java.util.Set;

/**
 * @author NMMkhungo
 * @since 2025/09/28
 **/

@Component
public class ComplianceDataInitializer {

    private final ComplianceRegionRepository complianceRegionRepository;

    public ComplianceDataInitializer (ComplianceRegionRepository complianceRegionRepository){
        this.complianceRegionRepository=complianceRegionRepository;
    }

    @PostConstruct
    @Transactional
    public void init() {
        // Initialize South Africa compliance data
        ComplianceRegion southAfrica = new ComplianceRegion();
        southAfrica.setRegionCode("southAfrica");
        southAfrica.setTaxAuthority("SARS");
        southAfrica.setVatRate(0.15);
        southAfrica.setWithholdingTaxRate(0.15);
        southAfrica.setReportingThreshold(50000.0);

        // Add compliance flags
        Set<ComplianceFlag> flags = new HashSet<>();
        flags.add(new ComplianceFlag("POPIA", true, southAfrica));
        flags.add(new ComplianceFlag("FICA", true, southAfrica));
        flags.add(new ComplianceFlag("financialSurveillance", true, southAfrica));
        southAfrica.setComplianceFlags(flags);

        // Add similar initialization for other regions...

        complianceRegionRepository.save(southAfrica);
    }
}
