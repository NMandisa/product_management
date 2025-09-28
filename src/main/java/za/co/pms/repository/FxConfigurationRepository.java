package za.co.pms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import za.co.pms.model.settings.currency.FxConfiguration;

/**
 * @author NMMkhungo
 * @since 2025/09/28
 **/
@Repository
public interface FxConfigurationRepository  extends JpaRepository<FxConfiguration, Long> {
}