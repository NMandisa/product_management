package za.co.pms.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import za.co.pms.enums.PriceType;
import za.co.pms.enums.PromotionType;
import za.co.pms.exception.CurrencyNotSupportedException;
import za.co.pms.model.compliance.Auditable;
import za.co.pms.model.product.Price;
import za.co.pms.model.product.PriceChange;
import za.co.pms.model.product.Variant;
import za.co.pms.model.promotion.Rule;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @author NMMkhungo
 * @since 2025/09/14
 **/
@Getter
@Setter
@Entity
@Table(name = "promotions")
@DiscriminatorValue("prices_table")
public class Promotion extends Auditable implements Serializable {
    @Id
    @Column(nullable = false)
    private Long id;

    @NotBlank
    @Size(max = 255)
    private String name;

    // Localized descriptions
    @ElementCollection
    @CollectionTable(name = "promotion_localizations",
            joinColumns = @JoinColumn(name = "promotion_id"))
    @MapKeyColumn(name = "language_code")
    @Column(name = "description", columnDefinition = "TEXT")
    private Map<String, String> localizedDescriptions = new HashMap<>();

    // Currency-specific discount values
    @ElementCollection
    @CollectionTable(name = "promotion_currency_discounts",
            joinColumns = @JoinColumn(name = "promotion_id"))
    @MapKeyColumn(name = "currency_code")
    @Column(name = "discount_value", precision = 19, scale = 4)
    private Map<String, BigDecimal> currencyDiscounts = new HashMap<>();

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
    private PromotionType type;

    // Regional restrictions - use String for region codes
    @ElementCollection
    @CollectionTable(name = "promotion_allowed_regions")
    @Column(name = "region_code")
    private Set<String> allowedRegions = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "promotion_excluded_regions")
    @Column(name = "region_code")
    private Set<String> excludedRegions = new HashSet<>();

    // Currency restrictions
    @ElementCollection
    @CollectionTable(name = "promotion_allowed_currencies")
    @Column(name = "currency_code")
    private Set<String> allowedCurrencies = new HashSet<>();

    // Core promotion parameters
    @Min(value = 1)
    private Integer requiredQuantity;

    @Min(value = 0)
    private Integer freeQuantity;

    @DecimalMin(value = "0.0")
    private BigDecimal discountValue;

    // Relationships
    @OneToMany(mappedBy = "promotion", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PriceChange> priceChanges = new LinkedHashSet<>();

    @OneToMany(mappedBy = "promotion", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Rule> rules = new LinkedHashSet<>();

    // SA-Specific Compliance Fields
    private boolean cpaCompliantDisplay = true;

    // Promotion validity
    @NotNull
    @FutureOrPresent
    private LocalDateTime startDate;

    @Future
    private LocalDateTime endDate;

    // Business methods with currency awareness
    public boolean isEligibleForRegion(String regionCode) {
        if (!excludedRegions.isEmpty() && excludedRegions.contains(regionCode)) {
            return false;
        }
        return allowedRegions.isEmpty() || allowedRegions.contains(regionCode);
    }

    public boolean isEligibleForCurrency(String currencyCode) {
        return allowedCurrencies.isEmpty() || allowedCurrencies.contains(currencyCode);
    }

    public BigDecimal getDiscountValue(String currencyCode) {
        return currencyDiscounts.getOrDefault(currencyCode, discountValue);
    }

    public String getCpaCompliantDescription() {
        return switch (type) {
            case BOGO -> String.format("Buy %d, Get %d FREE (%.2f%% saving)",
                    requiredQuantity, freeQuantity, calculateSavingsPercentage());
            case MULTIBUY -> String.format("Get %d for the price of %d (%.2f%% saving)",
                    requiredQuantity + freeQuantity, requiredQuantity, calculateSavingsPercentage());
            case FREE_SAMPLE -> "FREE sample with purchase (zero-rated for VAT)";
            case PERCENTAGE -> String.format("%.0f%% OFF", discountValue);
            case FIXED -> String.format("%s OFF", formatCurrency(discountValue, "ZAR"));
            default -> name;
        };
    }

    private String formatCurrency(BigDecimal amount, String currencyCode) {
        // Use your currency formatting service here
        //return CurrencyFormatter.format(amount, currencyCode);
        return null;
    }

    public boolean isActive() {
        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(startDate) && (endDate == null || now.isBefore(endDate));
    }

    public BigDecimal calculateSavingsPercentage() {
        if (requiredQuantity == null || freeQuantity == null || requiredQuantity == 0) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(freeQuantity)
                .divide(BigDecimal.valueOf(requiredQuantity + freeQuantity), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    public Price applyToVariant(Variant variant, String targetCurrencyCode) {
        if (!isEligibleForCurrency(targetCurrencyCode)) {
            throw new CurrencyNotSupportedException(
                    "Promotion not available for currency: " + targetCurrencyCode);
        }

        Price currentPrice = variant.getCurrentPrice();
        BigDecimal discount = getDiscountValue(targetCurrencyCode);

        Price discountedPrice = createDiscountedPrice(currentPrice, discount, targetCurrencyCode);
        discountedPrice.setPriceSource("PROMOTION-" + this.id);

        return discountedPrice;
    }

    private Price createDiscountedPrice(Price basePrice, BigDecimal discount, String currencyCode) {
        BigDecimal discountedAmount = calculateDiscountedAmount(basePrice.getFinalPrice(), discount);

        Price newPrice = new Price();
        newPrice.setVariant(basePrice.getVariant());
        newPrice.setBasePrice(discountedAmount);
        newPrice.setCurrencyCode(currencyCode);
        newPrice.setPriceType(PriceType.PROMOTIONAL);
        newPrice.setTaxClass(basePrice.getTaxClass());
        //newPrice.setVatRate(basePrice.getVatRate());
        newPrice.setEffectiveFrom(LocalDateTime.now());
        newPrice.setCurrent(true);

        return newPrice;
    }

    private BigDecimal calculateDiscountedAmount(BigDecimal baseAmount, BigDecimal discount) {
        return switch (type) {
            case PERCENTAGE -> baseAmount.multiply(
                    BigDecimal.ONE.subtract(discount.divide(new BigDecimal(100), 4, RoundingMode.HALF_UP)));
            case FIXED -> baseAmount.subtract(discount).max(BigDecimal.ZERO);
            default -> baseAmount;
        };
    }

    public String getLocalizedDescription(String languageCode, String regionCode) {
        String specificKey = languageCode + "_" + regionCode;
        if (localizedDescriptions.containsKey(specificKey)) {
            return localizedDescriptions.get(specificKey);
        }
        return localizedDescriptions.getOrDefault(languageCode, getCpaCompliantDescription());
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

    public void addPriceChange(PriceChange priceChange) {
        priceChanges.add(priceChange);
        priceChange.setPromotion(this);
    }
}