package za.co.pms.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import za.co.pms.enums.RegionCode;
import za.co.pms.exception.RegionNotFoundException;
import za.co.pms.model.settings.compliance.ComplianceRegion;
import za.co.pms.repository.ComplianceRegionRepository;

import java.math.BigDecimal;

/**
 * @author NMMkhungo
 * @since 2025/09/30
 **/
@Slf4j
@Component
@Transactional
@RequiredArgsConstructor
public class ComplianceRegionUtil {
    private final ComplianceRegionRepository complianceRegionRepository;

    @Transactional(readOnly = true)
    public ComplianceRegion getRegionWithFullDetails(RegionCode regionCode) {
        return complianceRegionRepository.findWithDetailsByRegionCode(regionCode)
                .orElseThrow(() -> new RegionNotFoundException(regionCode.toString()));
    }

    public Page<ComplianceRegion> getHighComplianceRegions(int page, int size) {
        return complianceRegionRepository.findByMinComplianceScore(
                new BigDecimal("70"), PageRequest.of(page, size,
                        Sort.by("complianceScore").descending()));
    }
}
