package za.co.pms.model.product;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinTable(
            name = "product_has_variants",
            joinColumns = @JoinColumn(name = "variant_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "product_id", referencedColumnName = "id",foreignKey=@ForeignKey(name = "product_variants_fk")
            ))
    private Product product;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "sku_id")
    private Sku sku;

    @OneToMany(mappedBy = "productVariant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<StockAllocation> stockAllocations = new HashSet<>();

    @OneToMany(mappedBy = "variant")
    private Set<Price> prices = new HashSet<>();

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
        allocation.setProductVariant(this);
    }

    public void removeStockAllocation(StockAllocation allocation) {
        stockAllocations.remove(allocation);
        allocation.setProductVariant(null);
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
