package za.co.pms.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;
import za.co.pms.enums.PriceType;
import za.co.pms.enums.PromotionType;
import za.co.pms.model.product.Price;
import za.co.pms.model.product.PriceChange;
import za.co.pms.model.product.Variant;
import za.co.pms.model.promotion.Rule;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author NMMkhungo
 * @since 2025/09/14
 **/
@Entity
@Table(name = "promotions")
@Getter
@Setter
public class Promotion {
    @Id
    @Column(nullable = false)
    private Long id;

    @NotBlank
    @Size(max = 255)
    private String name;

    @NotBlank
    @Size(max = 1000)
    private String description;

    // SA-Specific Compliance Fields
    @Pattern(regexp = "SARS\\d{9}")
    private String sarsComplianceCode;

    @Pattern(regexp = "CPA\\d{12}")
    private String cpaReferenceNumber;

    @PastOrPresent
    private LocalDateTime complianceApprovalDate;

    @Size(max = 255)
    private String approvedBy;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(nullable = false)
    private PromotionType type; // {BOGO, MULTIBUY, FREE_SAMPLE, PERCENTAGE, FIXED}

    // Core promotion parameters with validation groups
    @Min(value = 1)
    private Integer requiredQuantity;

    @Min(value = 0)
    private Integer freeQuantity;

    @DecimalMin(value = "0.0")
    private BigDecimal discountValue;

    // One-to-Many relationship with PriceChanges
    @OneToMany(mappedBy = "promotion", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PriceChange> priceChanges = new LinkedHashSet<>();

    // SA-Specific Compliance Fields
    @Column(nullable = false)
    private boolean cpaCompliantDisplay; // Must be true for SA launches

    // Promotion validity
    @NotNull
    @FutureOrPresent
    private LocalDateTime startDate;

    @Future
    private LocalDateTime endDate;

    // One-to-Many relationship with PromotionRules
    @OneToMany(mappedBy = "promotion", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Rule> rules =new LinkedHashSet<>();

    // SA-Specific Business Methods
    public boolean isEligibleForVariant(Variant variant) {
        // Check if promotion is active
        if (!isActive()) {
            return false;
        }

        // Check variant-specific rules
        if (rules.stream().anyMatch(rule -> !rule.isSatisfiedBy(variant))) {
            return false;
        }

        // Check regulatory compliance
        return !variant.hasRestrictedCategory() || isCompliantWithRestrictions();
    }

    // Business Methods
    public String getCpaCompliantDescription() {
        return switch (type) {
            case BOGO -> String.format("Buy %d, Get %d FREE (%.2f%% saving)",
                    requiredQuantity, freeQuantity, calculateSavingsPercentage());
            case MULTIBUY -> String.format("Get %d for the price of %d (%.2f%% saving)",
                    requiredQuantity + freeQuantity, requiredQuantity, calculateSavingsPercentage());
            case FREE_SAMPLE -> "FREE sample with purchase (zero-rated for VAT)";
            default -> description;
        };
    }

    public boolean isActive() {
        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(startDate) && (endDate == null || now.isBefore(endDate));
    }

    public BigDecimal calculateSavingsPercentage() {
        return BigDecimal.valueOf(freeQuantity)
                .divide(BigDecimal.valueOf(requiredQuantity + freeQuantity), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    public Price createDiscountedPrice(Variant variant, Price currentPrice) {
        BigDecimal discountedPrice = calculateDiscountedPrice(currentPrice.getBasePrice());

        Price price = new Price();
        price.setVariant(variant);
        price.setBasePrice(discountedPrice);
        price.setTaxClass(currentPrice.getTaxClass());
        price.setEffectiveFrom(LocalDateTime.now());
        price.setPriceType(PriceType.PROMOTIONAL);
        price.setPriceSource("PROMOTION-" + this.id);
        price.setCurrent(true);
        return price;
    }

    // SA-Specific Compliance Check
    public boolean isCpaCompliant() {
        return !StringUtils.isEmpty(description) &&
                description.contains("Price includes 15% VAT");
    }

    private BigDecimal calculateDiscountedPrice(BigDecimal basePrice) {
        return switch (type) {
            //case BOGO, MULTIBUY -> calculateEffectivePrice(new Price(basePrice));
            case PERCENTAGE -> basePrice.multiply(BigDecimal.ONE.subtract(
                    discountValue.divide(new BigDecimal(100), 4, RoundingMode.HALF_UP)));
            case FIXED -> basePrice.subtract(discountValue).max(BigDecimal.ZERO);
            case FREE_SAMPLE -> BigDecimal.ZERO;
            default -> basePrice;
        };
    }

    @PrePersist
    @PreUpdate
    private void validate() {
        if (type == PromotionType.BOGO || type == PromotionType.MULTIBUY) {
            if (requiredQuantity == null || requiredQuantity <= 0) {
                throw new IllegalArgumentException("Required quantity must be positive for BOGO/MULTIBUY promotions");
            }
            if (freeQuantity == null || freeQuantity <= 0) {
                throw new IllegalArgumentException("Free quantity must be positive for BOGO/MULTIBUY promotions");
            }
        }

        if (type == PromotionType.PERCENTAGE || type == PromotionType.FIXED) {
            if (discountValue == null || discountValue.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Discount value must be positive for PERCENTAGE/FIXED promotions");
            }
        }
    }

    public BigDecimal calculateEffectivePrice(Price basePrice) {
        return switch (type) {
            case BOGO, MULTIBUY -> basePrice.getBasePrice()
                    .multiply(new BigDecimal(requiredQuantity))
                    .divide(new BigDecimal(requiredQuantity + freeQuantity), 2, RoundingMode.HALF_UP);
            default -> basePrice.getBasePrice();
        };
    }

    public boolean isCompliantWithRestrictions() {
        // Implement SA-specific regulatory checks
        return cpaCompliantDisplay &&
                description != null &&
                description.contains("Price includes 15% VAT");
    }

    public void addPriceChange(PriceChange priceChange) {
        priceChanges.add(priceChange);
        priceChange.setPromotion(this);
    }

}