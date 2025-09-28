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
@Table(name = "number_format_config")
@AllArgsConstructor
@NoArgsConstructor
public class NumberFormatConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "region_code", length = 5)
    private String regionCode; // ZA, NG, FR, null for default

    @Column(name = "decimal_separator", nullable = false)
    private String decimalSeparator;

    @Column(name = "thousands_separator", nullable = false)
    private String thousandsSeparator;

    @Column(name = "grouping_pattern", length = 20)
    private String groupingPattern; // Store as JSON array string: "[3,3]"

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cultural_adaptations_id")
    private CulturalAdaptations culturalAdaptations;

    public NumberFormatConfig(String regionCode, String decimalSeparator, String thousandsSeparator,
                              String groupingPattern, CulturalAdaptations culturalAdaptations) {
        this.regionCode = regionCode;
        this.decimalSeparator = decimalSeparator;
        this.thousandsSeparator = thousandsSeparator;
        this.groupingPattern = groupingPattern;
        this.culturalAdaptations = culturalAdaptations;
    }
}
