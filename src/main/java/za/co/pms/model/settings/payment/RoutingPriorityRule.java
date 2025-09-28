package za.co.pms.model.settings.payment;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

/**
 * @author NMMkhungo
 * @since 2025/09/28
 **/
@Setter
@Getter
@Entity
@Table(name = "routing_priority_rule")
@NoArgsConstructor
@AllArgsConstructor
public class RoutingPriorityRule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "weight", nullable = false)
    private Integer weight;

    @Column(name = "condition_json", columnDefinition = "TEXT")
    private String conditionJson; // Store complex conditions as JSON

    @OneToMany(mappedBy = "routingRule", cascade = CascadeType.ALL)
    private Set<RoutingPreferredProvider> preferredProviders = new HashSet<>();
}
