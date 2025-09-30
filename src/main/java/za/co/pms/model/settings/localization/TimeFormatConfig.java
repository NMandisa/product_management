package za.co.pms.model.settings.localization;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.pms.enums.RegionCode;

/**
 * @author NMMkhungo
 * @since 2025/09/28
 **/
@Getter
@Setter
@Entity
@Table(name = "time_format_config")
@AllArgsConstructor
@NoArgsConstructor
public class TimeFormatConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "region_code", length = 5)
    @Enumerated(EnumType.STRING)
    private RegionCode regionCode; // ZA, NG, KE, null for default

    @Column(name = "time_format", nullable = false)
    private String timeFormat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cultural_adaptations_id")
    private CulturalAdaptations culturalAdaptations;

    public TimeFormatConfig(RegionCode regionCode, String timeFormat, CulturalAdaptations culturalAdaptations) {
        this.regionCode = regionCode;
        this.timeFormat = timeFormat;
        this.culturalAdaptations = culturalAdaptations;
    }
}
