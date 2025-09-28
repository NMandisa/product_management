package za.co.pms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import za.co.pms.model.settings.Currency;

import java.util.List;
import java.util.Optional;

/**
 * @author NMMkhungo
 * @since 2025/09/28
 **/
@Repository
public interface CurrencyRepository  extends JpaRepository<Currency, String> {
    @Query("SELECT c FROM Currency c WHERE c.status = 'ACTIVE'")
    List<Currency> findActiveCurrencies();

    @Query("SELECT c FROM Currency c WHERE c.region = :region")
    List<Currency> findByRegion(@Param("region") String region);

    @Query("SELECT c FROM Currency c WHERE c.code = :code")
    Optional<Currency> findByCurrencyCode(@Param("code") String code);
}