package za.co.pms.repository;

import io.micrometer.common.lang.Nullable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import za.co.pms.enums.RegionCode;
import za.co.pms.model.settings.localization.DateFormatConfig;

import java.util.List;
import java.util.Optional;

/**
 * @author NMMkhungo
 * @since 2025/09/28
 **/
@Repository
public interface DateFormatConfigRepository extends JpaRepository<DateFormatConfig, Long> {
    /*@Query("SELECT df FROM DateFormatConfig df WHERE df.regionCode = :regionCode OR df.regionCode IS NULL ORDER BY df.regionCode DESC")
    List<DateFormatConfig> findDateFormatByRegion(@Param("regionCode") String regionCode);*/
    // Throws exception if not found
   /* DateFormatConfig findByRegionCode(RegionCode regionCode);
    List<DateFormatConfig> findDateFormatByRegion (RegionCode regionCode);*/

    // Returns null if not found
    @Nullable
    DateFormatConfig findOptionalByRegionCode(@Nullable RegionCode regionCode);

    // Uses Optional for safe null handling
    Optional<DateFormatConfig> findSafeByRegionCode(RegionCode regionCode);
}
