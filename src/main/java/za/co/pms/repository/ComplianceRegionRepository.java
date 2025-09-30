package za.co.pms.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import za.co.pms.enums.ComplianceLevel;
import za.co.pms.enums.RegionCode;
import za.co.pms.model.settings.compliance.ComplianceRegion;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * @author NMMkhungo
 * @since 2025/09/28
 **/
@Repository
public interface ComplianceRegionRepository extends JpaRepository<ComplianceRegion, RegionCode> {

    // Find by primary key
    Optional<ComplianceRegion> findByRegionCode(RegionCode regionCode);

    // Find multiple by enum set
    List<ComplianceRegion> findByRegionCodeIn(Set<RegionCode> regions);

    // Derived queries still work
    List<ComplianceRegion> findByCpaEnforcedTrue();
    List<ComplianceRegion> findByBbrEnforcedTrue();
    List<ComplianceRegion> findByRequiresTaxInvoiceTrue();
    List<ComplianceRegion> findByTccValidFalse();

    // Numeric range queries
    List<ComplianceRegion> findByComplianceScoreBetween(BigDecimal min, BigDecimal max);

    // Sorting example
    List<ComplianceRegion> findByCpaEnforcedTrue(Sort sort);

    // Custom JPQL queries — only use fields on ComplianceRegion
    @Query("SELECT cr FROM ComplianceRegion cr WHERE cr.vatRate > 0")
    List<ComplianceRegion> findVATApplicableRegions();

    @Query("SELECT cr FROM ComplianceRegion cr WHERE cr.complianceScore >= :minScore")
    Page<ComplianceRegion> findByMinComplianceScore(@Param("minScore") BigDecimal minScore, Pageable pageable);

    @Query("SELECT COUNT(cr) FROM ComplianceRegion cr WHERE cr.vatRate > :vatThreshold")
    long countByVatRateGreaterThan(@Param("vatThreshold") BigDecimal vatThreshold);

    // Delete with caution
    @Modifying
    @Query("DELETE FROM ComplianceRegion cr WHERE cr.complianceScore < :minScore")
    int deleteByLowComplianceScore(@Param("minScore") BigDecimal minScore);

    Optional<ComplianceRegion> findWithDetailsByRegionCode(RegionCode regionCode);

    /*@Query("SELECT cr FROM ComplianceRegion cr " +
            "LEFT JOIN FETCH cr.additional_rules ar WHERE cr.region_code = :regionCode ")
    Optional<ComplianceRegion> findByRegionCodeWithAdditionalRules(RegionCode regionCode);*/

    /*@Query("""
    SELECT cr\s
    FROM ComplianceRegion cr\s
    LEFT JOIN FETCH cr.additionalRules ar
    LEFT JOIN FETCH ar.flags
    WHERE cr.regionCode = :regionCode
""")
    Optional<ComplianceRegion> findByRegionCodeWithAdditionalRules(@Param("regionCode") RegionCode regionCode);*/

}
