package za.co.pms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import za.co.pms.model.settings.compliance.ComplianceRegion;

import java.util.Optional;

/**
 * @author NMMkhungo
 * @since 2025/09/28
 **/
@Repository
public interface ComplianceRegionRepository extends JpaRepository<ComplianceRegion, String> {
    @Query("SELECT cr FROM ComplianceRegion cr LEFT JOIN FETCH cr.complianceFlags WHERE cr.regionCode = :regionCode")
    Optional<ComplianceRegion> findByRegionCodeWithFlags(@Param("regionCode") String regionCode);

    @Query("SELECT cr FROM ComplianceRegion cr LEFT JOIN FETCH cr.requiredLicenses WHERE cr.regionCode = :regionCode")
    Optional<ComplianceRegion> findByRegionCodeWithLicenses(@Param("regionCode") String regionCode);

    @Query("SELECT cr FROM ComplianceRegion cr LEFT JOIN FETCH cr.additionalRules ar LEFT JOIN FETCH ar.ruleFlags LEFT JOIN FETCH ar.ruleThresholds WHERE cr.regionCode = :regionCode")
    Optional<ComplianceRegion> findByRegionCodeWithAdditionalRules(@Param("regionCode") String regionCode);
}
