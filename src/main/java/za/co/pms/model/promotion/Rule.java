package za.co.pms.model.promotion;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import za.co.pms.enums.RuleType;
import za.co.pms.enums.ValueType;
import za.co.pms.model.Promotion;
import za.co.pms.model.product.Variant;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author NMMkhungo
 * @since 2025/09/14
 **/
@Slf4j
@Getter
@Setter
@Entity
public class Rule {
    @Id
    @Column(nullable = false)
    private Long id;

    private String name;
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinTable(
            name = "promotion_has_rules",
            joinColumns = @JoinColumn(name = "rule_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "promotion_id", referencedColumnName = "id",foreignKey=@ForeignKey(name = "promotion_rules_fk")
            ))
    private Promotion promotion;

    private String field;
    private String operator;
    private String value;

    // Rule state
    private boolean active = true;
    private boolean systemRule = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RuleType type;

    public boolean isSatisfiedBy(Variant variant) {
        // Implementation based on field, operator, value
        return true;
    }

    // for complex rule handling
    private String valueType; // "STRING", "NUMBER", "DATE", "BOOLEAN"
    private String valueFormat; // Format pattern for dates/numbers
    private String errorMessage; // Custom error message when rule fails
    // Error handling
    private String errorCode;
    // Rule priority (lower number = higher priority)
    private int priority; // Rule evaluation priority

    // For complex combinations
    private String parentRuleId; // For nested rules
    private String logicalOperator; // "AND", "OR" for compound rules
    private String childRuleIds; // Comma-separated IDs of child rules

    // SA-specific compliance
    private String complianceSection;
    // SA-Specific Compliance Fields
    private boolean cpaCompliant;
    private String regulatoryReference;
    private LocalDateTime complianceApprovalDate;
    private LocalDateTime complianceExpiryDate;

    // Audit fields
    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;


    public boolean isCompliant() {
        if (complianceExpiryDate != null && complianceExpiryDate.isBefore(LocalDateTime.now())) {
            return false;
        }

        return cpaCompliant && complianceApprovalDate != null;
    }

    public boolean hasChildRules() {
        return childRuleIds != null && !childRuleIds.trim().isEmpty();
    }

    private List<Rule> loadChildRules() {
        // Implementation to load child rules from IDs
        // This would typically use a RuleRepository
        return Collections.emptyList();
    }

}