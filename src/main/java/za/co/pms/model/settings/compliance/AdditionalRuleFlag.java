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
@Table(name = "additional_rule_flag")
public class AdditionalRuleFlag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "rule_name", nullable = false)
    private String ruleName; // crossBorderReporting, forexControls, etc.

    @Column(name = "rule_value", nullable = false)
    private Boolean ruleValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "additional_rules_id")
    private AdditionalRules additionalRules;
}