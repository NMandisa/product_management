package za.co.pms.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import za.co.pms.enums.Region;
import za.co.pms.model.product.Price;
import za.co.pms.model.product.Variant;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author NMMkhungo
 * @since 2025/09/13
 **/
@Getter
@Setter
@Entity
@Table(name = "products")
public class Product extends Auditable implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false)
    private Long id;

    @OneToMany(mappedBy = "product",cascade = CascadeType.ALL,fetch = FetchType.LAZY,orphanRemoval = true)
    @Fetch(FetchMode.SUBSELECT)
    private List<MediaAsset> mediaAssets= new ArrayList<>();

    @OneToMany(mappedBy = "product",cascade = CascadeType.ALL,fetch = FetchType.LAZY,orphanRemoval = true)
    @Fetch(FetchMode.SUBSELECT)
    private List<Variant> variants= new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id")
    private Vendor vendor;


    // Base fields (default language - English)
    private String name;
    private String description;

    // Localized fields
    @ElementCollection
    @CollectionTable(name = "product_localizations",
            joinColumns = @JoinColumn(name = "product_id"))
    @MapKeyColumn(name = "language_code")
    @Column(name = "name")
    private Map<String, String> localizedNames = new HashMap<>();

    @ElementCollection
    @CollectionTable(name = "product_localizations",
            joinColumns = @JoinColumn(name = "product_id"))
    @MapKeyColumn(name = "language_code")
    @Column(name = "description")
    private Map<String, String> localizedDescriptions = new HashMap<>();

    // Regional availability
    @ElementCollection
    @CollectionTable(name = "product_available_regions")
    @Column(name = "region_code")
    private Set<String> availableRegions = new HashSet<>();

    // Business methods with localization
    public String getLocalizedName(String languageCode, Region regionCode) {
        // Try specific region variant first
        String regionSpecificKey = languageCode + "_" + regionCode;
        if (localizedNames.containsKey(regionSpecificKey)) {
            return localizedNames.get(regionSpecificKey);
        }
        // Fallback to language only
        return localizedNames.getOrDefault(languageCode, name);
    }

    public String getLocalizedDescription(String languageCode, Region regionCode) {
        String regionSpecificKey = languageCode + "_" + regionCode;
        if (localizedDescriptions.containsKey(regionSpecificKey)) {
            return localizedDescriptions.get(regionSpecificKey);
        }
        return localizedDescriptions.getOrDefault(languageCode, description);
    }

    public boolean isAvailableInRegion(Region regionCode) {
        return availableRegions.isEmpty() ;
    }

    // Add currency-aware pricing methods
    public Price getPriceRangeInCurrency(String currencyCode) {
        List<Price> prices = variants.stream()
                .flatMap(variant -> variant.getPrices().stream())
                .filter(price -> currencyCode.equals(price.getCurrencyCode()))
                .filter(Price::isCurrent)
                .filter(Price::isEffective)
                .toList();

        if (prices.isEmpty()) return null;

        BigDecimal min = prices.stream()
                .map(Price::getFinalPrice)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        BigDecimal max = prices.stream()
                .map(Price::getFinalPrice)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        Price rangePrice = new Price();
        rangePrice.setBasePrice(min);
        rangePrice.setSalePrice(max);
        rangePrice.setCurrencyCode(currencyCode);
        return rangePrice;
    }

    // Helper methods remain the same
    public void addVariant(Variant variant) {
        variants.add(variant);
        variant.setProduct(this);
    }

    public void removeVariant(Variant variant) {
        variants.remove(variant);
        variant.setProduct(null);
    }

    public int getTotalStock() {
        return variants.stream()
                .mapToInt(Variant::getTotalStock)
                .sum();
    }
}
