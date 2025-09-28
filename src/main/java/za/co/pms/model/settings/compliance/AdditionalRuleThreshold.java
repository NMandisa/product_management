package za.co.pms.model.settings.compliance;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * @author NMMkhungo
 * @since 2025/09/28
 **/
@Getter
@Setter
@Entity
@Table(name = "additional_rule_threshold")
public class AdditionalRuleThreshold {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "threshold_name", nullable = false)
    private String thresholdName; // suspiciousActivityThreshold, recordKeepingYears, etc.

    @Column(name = "threshold_value", nullable = false)
    private Double thresholdValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "additional_rules_id")
    private AdditionalRules additionalRules;
}