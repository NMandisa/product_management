package za.co.pms.model.settings.compliance;

import jakarta.persistence.*;
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
@Table(name = "compliance_flag")
@NoArgsConstructor
public class ComplianceFlag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "flag_name", nullable = false)
    private String name; // POPIA, FICA, NDPR, etc.

    @Column(name = "flag_value", nullable = false)
    private Boolean value;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_code")
    private ComplianceRegion complianceRegion;

    public ComplianceFlag(String name, boolean value, ComplianceRegion complianceRegion) {
        this.name=name;
        this.value=value;
        this.complianceRegion=complianceRegion;
    }
}
