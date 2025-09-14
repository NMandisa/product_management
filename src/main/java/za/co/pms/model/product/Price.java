package za.co.pms.model.product;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import za.co.pms.enums.PriceType;
import za.co.pms.enums.TaxType;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

/**
 * @author NMMkhungo
 * @since 2025/09/14
 **/
@Getter
@Setter
@Entity
public class Price {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id", foreignKey = @ForeignKey(name = "fk_price_variant"))
    private Variant variant;

    @Column(precision = 19, scale = 2, nullable = false)
    private BigDecimal basePrice; // Excluding VAT

    @Column(nullable = false)
    private boolean current;

    @ManyToOne
    @JoinColumn(name = "tax_class_id")
    private TaxClass taxClass; // {STANDARD, ZERO_RATED, EXEMPT}

    @Column(nullable = false)
    private LocalDateTime effectiveFrom;

    private LocalDateTime effectiveTo;

    @Enumerated(EnumType.STRING)
    private PriceType priceType; // {REGULAR, PROMOTIONAL, SEASONAL}

    @Column(length = 50)
    private String priceSource; // "PROMOTION-2024-Q3", "SEASONAL-WINTER"

    // SA-Specific Audit Fields
    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedBy
    private String updatedBy;

    // Business Methods
    public boolean isActive() {
        return current &&
                (effectiveTo == null || effectiveTo.isAfter(LocalDateTime.now()));
    }

    public BigDecimal getDisplayPrice() {
        return calculateDisplayPrice(basePrice, taxClass);
    }

    private static BigDecimal calculateDisplayPrice(BigDecimal basePrice, TaxClass taxClass) {
        if (taxClass == null) {
            return basePrice.setScale(2, RoundingMode.HALF_UP);
        }

        BigDecimal taxAmount = taxClass.calculateTax(basePrice);
        return basePrice.add(taxAmount).setScale(2, RoundingMode.HALF_UP);
    }

}
