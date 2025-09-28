package za.co.pms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import za.co.pms.model.settings.payment.RoutingPriorityRule;

import java.util.List;

/**
 * @author NMMkhungo
 * @since 2025/09/28
 **/
@Repository
public interface RoutingPriorityRuleRepository  extends JpaRepository<RoutingPriorityRule, Long> {
    @Query("SELECT r FROM RoutingPriorityRule r ORDER BY r.weight DESC")
    List<RoutingPriorityRule> findAllOrderByWeightDesc();
}
