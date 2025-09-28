package za.co.pms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import za.co.pms.model.settings.localization.TimeFormatConfig;

import java.util.List;

/**
 * @author NMMkhungo
 * @since 2025/09/28
 **/
@Repository
public interface TimeFormatConfigRepository  extends JpaRepository<TimeFormatConfig, Long> {
    @Query("SELECT tf FROM TimeFormatConfig tf WHERE tf.regionCode = :regionCode OR tf.regionCode IS NULL ORDER BY tf.regionCode DESC")
    List<TimeFormatConfig> findTimeFormatByRegion(@Param("regionCode") String regionCode);
}
