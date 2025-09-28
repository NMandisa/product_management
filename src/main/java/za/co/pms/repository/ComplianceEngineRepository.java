package za.co.pms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import za.co.pms.model.settings.ComplianceEngine;

import java.util.Optional;

/**
 * @author NMMkhungo
 * @since 2025/09/28
 **/
@Repository
public interface ComplianceEngineRepository  extends JpaRepository<ComplianceEngine, Long> {
    @Query("SELECT ce FROM ComplianceEngine ce ORDER BY ce.id DESC LIMIT 1")
    Optional<ComplianceEngine> findLatestConfiguration();
}
