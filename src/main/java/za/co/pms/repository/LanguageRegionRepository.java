package za.co.pms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import za.co.pms.enums.RegionCode;
import za.co.pms.model.settings.localization.LanguageRegion;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * @author NMMkhungo
 * @since 2025/09/30
 **/
@Repository
public interface LanguageRegionRepository  extends JpaRepository<LanguageRegion, Long> {
    List<LanguageRegion> findByRegionCode(RegionCode regionCode);

    List<LanguageRegion> findByRegionCodeAndIsDefaultTrue(RegionCode regionCode);

    @Query("SELECT lr FROM LanguageRegion lr WHERE lr.regionCode IN :regions")
    List<LanguageRegion> findByRegions(@Param("regions") Set<RegionCode> regions);

    @Query("SELECT lr FROM LanguageRegion lr WHERE lr.language.code = :languageCode AND lr.regionCode = :regionCode")
    Optional<LanguageRegion> findByLanguageAndRegion(@Param("languageCode") String languageCode,
                                                     @Param("regionCode") RegionCode regionCode);
}
