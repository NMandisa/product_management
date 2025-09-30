package za.co.pms.data;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import za.co.pms.enums.RegionCode;
import za.co.pms.model.settings.compliance.ComplianceFlag;
import za.co.pms.model.settings.compliance.ComplianceRegion;
import za.co.pms.repository.ComplianceRegionRepository;

import java.util.HashSet;
import java.util.Set;

/**
 * @author NMMkhungo
 * @since 2025/09/28
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class ComplianceDataInitializer {

    private final ComplianceRegionRepository complianceRegionRepository;

    @PostConstruct
    public void init() {
        try {
            initializeComplianceData();
        } catch (Exception e) {
            log.error("Failed to initialize compliance data", e);
            // Depending on your requirements, you might want to re-throw
            // the exception to prevent the application from starting
            // throw new RuntimeException("Compliance data initialization failed", e);
        }
    }

    @Transactional
    protected void initializeComplianceData() {
        initializeSouthAfrica();
        initializeNigeria();
        initializeKenya();
        // Add other regions as needed
    }

    private void initializeSouthAfrica() {
        // Check if data already exists to prevent duplicates
        if (complianceRegionRepository.findById(RegionCode.ZA).isEmpty()) {
            ComplianceRegion southAfrica = ComplianceRegion.createSouthAfrica();

            // Add compliance flags
            Set<ComplianceFlag> flags = new HashSet<>();
            flags.add(createComplianceFlag("POPIA", southAfrica));
            flags.add(createComplianceFlag("FICA", southAfrica));
            flags.add(createComplianceFlag("Financial Surveillance", southAfrica));

            southAfrica.setComplianceFlags(flags);
            complianceRegionRepository.save(southAfrica);
            log.info("South Africa compliance data initialized");
        } else {
            log.info("South Africa compliance data already exists, skipping initialization");
        }
    }

    private void initializeNigeria() {
        if (complianceRegionRepository.findById(RegionCode.NG).isEmpty()) {
            ComplianceRegion nigeria = ComplianceRegion.createNigeria();
            // Add Nigeria-specific flags and configurations
            complianceRegionRepository.save(nigeria);
            log.info("Nigeria compliance data initialized");
        }
    }

    private void initializeKenya() {
        if (complianceRegionRepository.findById(RegionCode.KE).isEmpty()) {
            ComplianceRegion kenya = ComplianceRegion.createKenya();
            // Add Kenya-specific flags and configurations
            complianceRegionRepository.save(kenya);
            log.info("Kenya compliance data initialized");
        }
    }

    private ComplianceFlag createComplianceFlag(String name, ComplianceRegion region) {
        ComplianceFlag flag = new ComplianceFlag();
        flag.setName(name); // Matches the 'name' field in ComplianceFlag
        flag.setValue(true); // Matches the 'value' field in ComplianceFlag
        flag.setComplianceRegion(region);
        return flag;
    }
}
