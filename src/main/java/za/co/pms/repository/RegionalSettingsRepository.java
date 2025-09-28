package za.co.pms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import za.co.pms.model.settings.localization.RegionalSettings;

import java.util.Optional;

/**
 * @author NMMkhungo
 * @since 2025/09/28
 **/
@Repository
public interface RegionalSettingsRepository extends JpaRepository<RegionalSettings, Long> {
    @Query("SELECT rs FROM RegionalSettings rs WHERE rs.regionGroup = :regionGroup")
    Optional<RegionalSettings> findByRegionGroup(@Param("regionGroup") String regionGroup);

    @Query("SELECT rs FROM RegionalSettings rs LEFT JOIN FETCH rs.fallbackLanguages WHERE rs.regionGroup = :regionGroup")
    Optional<RegionalSettings> findByRegionGroupWithFallbacks(@Param("regionGroup") String regionGroup);
}
