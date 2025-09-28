package za.co.pms.model.settings;

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
@Table(name = "compliance_engine")
@AllArgsConstructor
@NoArgsConstructor
public class ComplianceEngine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "real_time_screening", nullable = false)
    private Boolean realTimeScreening;

    @Column(name = "batch_processing", nullable = false)
    private Boolean batchProcessing;

    @Column(name = "risk_based_approach", nullable = false)
    private Boolean riskBasedApproach;

    @Column(name = "auto_reporting", nullable = false)
    private Boolean autoReporting;

    @Column(name = "audit_trail", nullable = false)
    private Boolean auditTrail;

    @Column(name = "version", nullable = false)
    private String version;
}
