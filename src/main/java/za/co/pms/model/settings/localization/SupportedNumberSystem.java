package za.co.pms.model.settings.localization;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author NMMkhungo
 * @since 2025/09/28
 **/
@Getter
@Setter
@Entity
@Table(name = "supported_number_system")
@AllArgsConstructor
@NoArgsConstructor
public class SupportedNumberSystem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "number_system", nullable = false)
    private String numberSystem; // latin, arabic, devanagari

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cultural_adaptations_id")
    private CulturalAdaptations culturalAdaptations;

    public SupportedNumberSystem(String numberSystem, CulturalAdaptations culturalAdaptations) {
        this.numberSystem = numberSystem;
        this.culturalAdaptations = culturalAdaptations;
    }
}
