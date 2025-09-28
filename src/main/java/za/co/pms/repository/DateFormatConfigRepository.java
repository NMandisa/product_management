package za.co.pms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import za.co.pms.model.settings.localization.DateFormatConfig;

import java.util.List;

/**
 * @author NMMkhungo
 * @since 2025/09/28
 **/
@Repository
public interface DateFormatConfigRepository extends JpaRepository<DateFormatConfig, Long> {
    @Query("SELECT df FROM DateFormatConfig df WHERE df.regionCode = :regionCode OR df.regionCode IS NULL ORDER BY df.regionCode DESC")
    List<DateFormatConfig> findDateFormatByRegion(@Param("regionCode") String regionCode);
}
