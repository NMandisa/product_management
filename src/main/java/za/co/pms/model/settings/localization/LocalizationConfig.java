package za.co.pms.model.settings.localization;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.pms.model.settings.LocalizationEngine;

/**
 * @author NMMkhungo
 * @since 2025/09/28
 **/
@Getter
@Setter
@Entity
@Table(name = "localization_config")
@AllArgsConstructor
@NoArgsConstructor
public class LocalizationConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "rtl_support", nullable = false)
    private Boolean rtlSupport;

    @Column(name = "locale_aware_formatting", nullable = false)
    private Boolean localeAwareFormatting;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "cultural_adaptations_id")
    private CulturalAdaptations culturalAdaptations;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "accessibility_id")
    private AccessibilitySettings accessibility;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "localization_engine_id")
    private LocalizationEngine localizationEngine;

    public void addCulturalAdaptations(CulturalAdaptations culturalAdaptations){
        culturalAdaptations.setLocalizationConfig(this);
    }
    public void removeCulturalAdaptations(CulturalAdaptations culturalAdaptations){
        culturalAdaptations.setLocalizationConfig(null);
    }

}
