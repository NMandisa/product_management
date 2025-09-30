package za.co.pms.model.settings.localization;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.pms.enums.Region;
import za.co.pms.enums.RegionCode;
import za.co.pms.model.compliance.Auditable;

import java.io.Serializable;

/**
 * @author NMMkhungo
 * @since 2025/09/28
 **/
@Setter
@Getter
@Entity
@Table(name = "language_region")
@AllArgsConstructor
@NoArgsConstructor
public class LanguageRegion extends Auditable implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "language_code")
    private Language language;

    @Enumerated(EnumType.STRING)
    @Column(name = "region_code", length = 5)
    private RegionCode regionCode;

    // Additional localization settings
    @Column(name = "is_default", nullable = false)
    private Boolean isDefault = false;

    @Column(name = "display_order")
    private Integer displayOrder = 0;

    @Column(name = "localized_name")
    private String localizedName;

    // SA-Specific compliance fields
    @Column(name = "sars_compliant", nullable = false)
    private Boolean sarsCompliant = false;

    @Column(name = "cpa_compliant", nullable = false)
    private Boolean cpaCompliant = false;

    public LanguageRegion(Language language, RegionCode regionCode) {
        this.language = language;
        this.regionCode = regionCode;
    }

    // Business methods
    public String getLocaleCode() {
        return language.getCode() + "-" + regionCode.getIsoCode();
    }

    public boolean isAfricanRegion() {
        return regionCode.getRegion() != Region.GLOBAL_RESERVE &&
                regionCode.getRegion() != Region.EMERGING_MARKETS;
    }

    public boolean requiresSACompliance() {
        return regionCode == RegionCode.ZA;
    }
}
