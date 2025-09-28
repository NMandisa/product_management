package za.co.pms.model.settings.payment;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author NMMkhungo
 * @since 2025/09/28
 **/
@Setter
@Getter
@Entity
@Table(name = "routing_preferred_provider")
@NoArgsConstructor
@AllArgsConstructor
public class RoutingPreferredProvider {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "routing_rule_id")
    private RoutingPriorityRule routingRule;

    @Column(name = "provider_id", nullable = false)
    private String providerId;

    @Column(name = "preference_order", nullable = false)
    private Integer preferenceOrder;

    public RoutingPreferredProvider(RoutingPriorityRule routingRule, String providerId, Integer preferenceOrder) {
        this.routingRule = routingRule;
        this.providerId = providerId;
        this.preferenceOrder = preferenceOrder;
    }
}
