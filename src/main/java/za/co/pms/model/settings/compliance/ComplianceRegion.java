package za.co.pms.model.settings.compliance;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

/**
 * @author NMMkhungo
 * @since 2025/09/28
 **/
@Getter
@Setter
@Entity
@Table(name = "compliance_region")
public class ComplianceRegion {
    @Id
    @Column(name = "region_code", length = 50)
    private String regionCode; // southAfrica, nigeria, kenya, etc.

    @Column(name = "tax_authority", nullable = false)
    private String taxAuthority;

    @Column(name = "vat_rate", nullable = false)
    private Double vatRate;

    @Column(name = "withholding_tax_rate", nullable = false)
    private Double withholdingTaxRate;

    @Column(name = "reporting_threshold", nullable = false)
    private Double reportingThreshold;

    @OneToMany(mappedBy = "complianceRegion", cascade = CascadeType.ALL)
    private Set<ComplianceFlag> complianceFlags = new HashSet<>();

    @OneToMany(mappedBy = "complianceRegion", cascade = CascadeType.ALL)
    private Set<RequiredLicense> requiredLicenses = new HashSet<>();

    @OneToOne(mappedBy = "complianceRegion", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private AdditionalRules additionalRules;
}
