package za.co.pms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import za.co.pms.model.settings.currency.RegionalHierarchy;

import java.util.Optional;

/**
 * @author NMMkhungo
 * @since 2025/09/28
 **/
@Repository
public interface RegionalHierarchyRepository extends JpaRepository<RegionalHierarchy, Long> {
    @Query("SELECT rh FROM RegionalHierarchy rh WHERE rh.name = :name")
    Optional<RegionalHierarchy> findByRegionName(@Param("name") String name);
}
