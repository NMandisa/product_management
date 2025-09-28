package za.co.pms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import za.co.pms.model.settings.payment.RegionProviderMapping;

import java.util.List;

/**
 * @author NMMkhungo
 * @since 2025/09/28
 **/
@Repository
public interface RegionProviderMappingRepository  extends JpaRepository<RegionProviderMapping, Long> {
    @Query("SELECT r FROM RegionProviderMapping r WHERE r.regionGroup = :regionGroup ORDER BY r.priorityOrder")
    List<RegionProviderMapping> findByRegionGroup(@Param("regionGroup") String regionGroup);
}
