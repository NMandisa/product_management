package za.co.pms.model.settings.localization;

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
@Table(name = "currency_placement_config")
@AllArgsConstructor
@NoArgsConstructor
public class CurrencyPlacementConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "default_placement", nullable = false)
    private String defaultPlacement; // before

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cultural_adaptations_id")
    private CulturalAdaptations culturalAdaptations;

    @OneToMany(mappedBy = "currencyPlacementConfig", cascade = CascadeType.ALL)
    private Set<CurrencyPlacementException> exceptions = new HashSet<>();

}
