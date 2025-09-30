package za.co.pms.model.product;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import za.co.pms.enums.PriceChangeType;
import za.co.pms.model.compliance.Auditable;
import za.co.pms.model.Promotion;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

/**
 * @author NMMkhungo
 * @since 2025/09/14
 **/
@Entity
@Table(name = "price_changes")
@Getter
@Setter
public class PriceChange extends Auditable implements Serializable {
    @Id
    @Column(nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promotion_id", foreignKey = @ForeignKey(name = "fk_price_change_promotion"))
    private Promotion promotion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "old_price_id", foreignKey = @ForeignKey(name = "fk_price_change_old_price"))
    private Price oldPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "new_price_id", foreignKey = @ForeignKey(name = "fk_price_change_new_price"))
    private Price newPrice;

    // Add missing fields
    @Column(name = "changed_at", nullable = false)
    private LocalDateTime changedAt;

    @Column(name = "change_reason", length = 500)
    private String changeReason;

    @Enumerated(EnumType.STRING)
    @Column(name = "change_type", nullable = false)
    private PriceChangeType changeType; // MANUAL, PROMOTIONAL, CURRENCY_CONVERSION, TAX_ADJUSTMENT

    @Column(name = "user_id")
    private String userId; // Who initiated the change

    @Column(name = "change_source")
    private String changeSource; // SYSTEM, ADMIN, API, BATCH

    // SA-Specific audit fields
    @Column(name = "sars_compliance_ref")
    private String sarsComplianceRef;

    @Column(name = "cpa_compliant")
    private Boolean cpaCompliant = true;

    // Business methods
    @PrePersist
    protected void onCreate() {
        if (changedAt == null) {
            changedAt = LocalDateTime.now();
        }
    }

    public BigDecimal getPriceDifference() {
        if (oldPrice != null && newPrice != null) {
            return newPrice.getFinalPrice().subtract(oldPrice.getFinalPrice());
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal getPercentageChange() {
        if (oldPrice != null && newPrice != null && oldPrice.getFinalPrice().compareTo(BigDecimal.ZERO) > 0) {
            return getPriceDifference()
                    .divide(oldPrice.getFinalPrice(), 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal(100));
        }
        return BigDecimal.ZERO;
    }

    public boolean isPriceIncrease() {
        return getPriceDifference().compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean isPriceDecrease() {
        return getPriceDifference().compareTo(BigDecimal.ZERO) < 0;
    }

}