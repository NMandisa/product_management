package za.co.pms.model.inventory.sku;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

/**
 * @author NMMkhungo
 * @since 2025/09/14
 **/
@Getter
@Setter
@Entity
public class Warehouse {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    private String name;
    private String location;
    private boolean active = true;

    @OneToMany(mappedBy = "warehouse", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<StockAllocation> stockAllocations = new HashSet<>();

    // Helper methods
    public void addStockAllocation(StockAllocation allocation) {
        stockAllocations.add(allocation);
        allocation.setWarehouse(this);
    }

    public void removeStockAllocation(StockAllocation allocation) {
        stockAllocations.remove(allocation);
        allocation.setWarehouse(null);
    }

    // Business methods
    public int getTotalInventory() {
        return stockAllocations.stream()
                .mapToInt(StockAllocation::getQuantity)
                .sum();
    }
}
