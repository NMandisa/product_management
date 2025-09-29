package za.co.pms.model.product;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import za.co.pms.exception.PriceNotFoundException;
import za.co.pms.model.Product;
import za.co.pms.model.inventory.sku.StockAllocation;

import java.util.HashSet;
import java.util.Set;

/**
 * @author NMMkhungo
 * @since 2025/09/14
 **/
@Getter
@Setter
@Entity
public class Variant {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false)
    private Long id;

    private String name;
    //@Column(columnDefinition = "json")
    //private Map<String, String> attributes;

    // Fix product relationship
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "sku_id")
    private Sku sku;

    @OneToMany(mappedBy = "variant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<StockAllocation> stockAllocations = new HashSet<>();

    @OneToMany(mappedBy = "variant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Price> prices = new HashSet<>();

    // Add method to get current price
    public Price getCurrentPrice() {
        return prices.stream()
                .filter(Price::isCurrent)
                .filter(Price::isEffective)
                .findFirst()
                .orElseThrow(() -> new PriceNotFoundException("No current price found for variant: " + id));
    }

    public Price getCurrentPriceInCurrency(String currencyCode) {
        return prices.stream()
                .filter(price -> currencyCode.equals(price.getCurrencyCode()))
                .filter(Price::isCurrent)
                .filter(Price::isEffective)
                .findFirst()
                .orElseThrow(() -> new PriceNotFoundException("No current price found for currency: " + currencyCode));
    }

    public void addPrice(Price price) {
        prices.add(price);
        price.setVariant(this);
    }

    public boolean hasRestrictedCategory() {
        // Implementation based on your business logic
        return false;
    }

    // Helper methods
    public void addStockAllocation(StockAllocation allocation) {
        stockAllocations.add(allocation);
        allocation.setVariant(this);
    }

    public void removeStockAllocation(StockAllocation allocation) {
        stockAllocations.remove(allocation);
        allocation.setVariant(null);
    }

    // Business methods
    public int getTotalStock() {
        return stockAllocations.stream()
                .mapToInt(StockAllocation::getQuantity)
                .sum();
    }

    public int getAvailableStock() {
        return stockAllocations.stream()
                .mapToInt(StockAllocation::getAvailableQuantity)
                .sum();
    }

}
