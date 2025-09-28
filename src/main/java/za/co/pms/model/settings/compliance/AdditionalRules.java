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
@Table(name = "additional_rules")
public class AdditionalRules {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_code")
    private ComplianceRegion complianceRegion;

    @OneToMany(mappedBy = "additionalRules", cascade = CascadeType.ALL)
    private Set<AdditionalRuleFlag> ruleFlags = new HashSet<>();

    @OneToMany(mappedBy = "additionalRules", cascade = CascadeType.ALL)
    private Set<AdditionalRuleThreshold> ruleThresholds = new HashSet<>();
}