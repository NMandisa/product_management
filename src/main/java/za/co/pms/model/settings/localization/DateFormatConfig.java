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
@Table(name = "date_format_config")
@AllArgsConstructor
@NoArgsConstructor
public class DateFormatConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "region_code", length = 5)
    private RegionCode regionCode; // ZA, NG, KE, null for default

    @Column(name = "date_format", nullable = false)
    private String dateFormat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cultural_adaptations_id")
    private CulturalAdaptations culturalAdaptations;

    public DateFormatConfig(RegionCode regionCode, String dateFormat, CulturalAdaptations culturalAdaptations) {
        this.regionCode = regionCode;
        this.dateFormat = dateFormat;
        this.culturalAdaptations = culturalAdaptations;
    }
}
