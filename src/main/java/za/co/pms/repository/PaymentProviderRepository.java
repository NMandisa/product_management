package za.co.pms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import za.co.pms.model.settings.PaymentProvider;

import java.util.List;

/**
 * @author NMMkhungo
 * @since 2025/09/28
 **/
@Repository
public interface PaymentProviderRepository extends JpaRepository<PaymentProvider, String> {
    @Query("SELECT p FROM PaymentProvider p JOIN p.regions pr WHERE pr.regionCode = :regionCode")
    List<PaymentProvider> findByRegion(@Param("regionCode") String regionCode);

    @Query("SELECT p FROM PaymentProvider p JOIN p.currencies pc WHERE pc.currencyCode = :currencyCode")
    List<PaymentProvider> findByCurrency(@Param("currencyCode") String currencyCode);

    @Query("SELECT p FROM PaymentProvider p WHERE p.status = 'active'")
    List<PaymentProvider> findActiveProviders();
}
