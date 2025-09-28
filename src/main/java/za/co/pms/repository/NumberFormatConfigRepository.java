package za.co.pms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import za.co.pms.model.settings.localization.NumberFormatConfig;

import java.util.List;

/**
 * @author NMMkhungo
 * @since 2025/09/28
 **/
@Repository
public interface NumberFormatConfigRepository extends JpaRepository<NumberFormatConfig, Long> {
    @Query("SELECT nf FROM NumberFormatConfig nf WHERE nf.regionCode = :regionCode OR nf.regionCode IS NULL ORDER BY nf.regionCode DESC")
    List<NumberFormatConfig> findNumberFormatByRegion(@Param("regionCode") String regionCode);
}
