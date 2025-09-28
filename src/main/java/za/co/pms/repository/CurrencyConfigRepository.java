package za.co.pms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import za.co.pms.model.settings.currency.CurrencyConfig;

/**
 * @author NMMkhungo
 * @since 2025/09/28
 **/
@Repository
public interface CurrencyConfigRepository extends JpaRepository<CurrencyConfig, Long> {
}