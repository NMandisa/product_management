package za.co.pms.model.product;

import jakarta.persistence.*;
import lombok.*;
import za.co.pms.enums.PriceType;
import za.co.pms.enums.TaxType;
import za.co.pms.model.Auditable;

import java.io.Serializable;
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
@Table(name = "prices")
@DiscriminatorValue("prices_table")
public class Price extends Auditable implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id", foreignKey = @ForeignKey(name = "fk_price_variant"))
    private Variant variant;

    @Column(name = "base_price", precision = 19, scale = 4)
    private BigDecimal basePrice; // Excluding VAT

    @Column(name = "sale_price", precision = 19, scale = 4)
    private BigDecimal salePrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "price_type", nullable = false)
    private PriceType priceType = PriceType.REGULAR; // {REGULAR, PROMOTIONAL, SEASONAL}

    // Currency information
    @Column(name = "currency_code", length = 3, nullable = false)
    private String currencyCode = "ZAR"; // Default from config

    @Column(name = "exchange_rate", precision = 19, scale = 6)
    private BigDecimal exchangeRate = BigDecimal.ONE;

    @Column(name = "price_source")
    private String priceSource; // "SYSTEM", "MANUAL", "PROMOTION" // "PROMOTION-2024-Q3", "SEASONAL-WINTER"

    @Column(nullable = false)
    private boolean current;

    @ManyToOne
    @JoinColumn(name = "tax_class_id")
    private TaxClass taxClass; // {STANDARD, ZERO_RATED, EXEMPT}

    @Column(nullable = false)
    private LocalDateTime effectiveFrom;

    private LocalDateTime effectiveTo;


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

    // Business methods
    public BigDecimal getFinalPrice() {
        return salePrice != null ? salePrice : basePrice;
    }

    public BigDecimal getVatInclusivePrice() {
        BigDecimal price = getFinalPrice();
        if (taxClass != null && taxClass.getTaxType() == TaxType.STANDARD) {
            //return price.add(price.multiply(vatRate));
        }
        return price;
    }

    public boolean isEffective() {
        LocalDateTime now = LocalDateTime.now();
        return (effectiveFrom == null || now.isAfter(effectiveFrom)) &&
                (effectiveTo == null || now.isBefore(effectiveTo));
    }

    public Price convertToCurrency(String targetCurrencyCode, BigDecimal exchangeRate) {
        Price convertedPrice = new Price();
        convertedPrice.setBasePrice(this.basePrice.multiply(exchangeRate));
        convertedPrice.setSalePrice(this.salePrice != null ?
                this.salePrice.multiply(exchangeRate) : null);
        convertedPrice.setCurrencyCode(targetCurrencyCode);
        convertedPrice.setExchangeRate(exchangeRate);
        convertedPrice.setPriceType(this.priceType);
        convertedPrice.setTaxClass(this.taxClass);
        //convertedPrice.setVatRate(this.vatRate);
        convertedPrice.setPriceSource("FX_CONVERSION");
        return convertedPrice;
    }

    // Format price according to currency rules
    public String getFormattedPrice() {
        //return CurrencyFormatter.format(this.getFinalPrice(), currencyCode);
        return null;
    }

    public String getFormattedVatInclusivePrice() {
        //return CurrencyFormatter.format(this.getVatInclusivePrice(), currencyCode);
        return null;
    }

}
