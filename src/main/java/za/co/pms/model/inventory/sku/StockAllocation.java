package za.co.pms.model.inventory.sku;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.pms.enums.StockStatus;
import za.co.pms.exception.InsufficientStockException;
import za.co.pms.model.compliance.Auditable;
import za.co.pms.model.product.Variant;

import java.io.Serializable;

/**
 * @author NMMkhungo
 * @since 2025/09/14
 **/

@Getter
@Setter
@Entity
@Table(name = "stock_allocation")
@AllArgsConstructor
@NoArgsConstructor
public class StockAllocation extends Auditable implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinTable(
            name = "warehouse_has_stock_allocations",
            joinColumns = @JoinColumn(name = "stock_allocation_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "warehouse_id", referencedColumnName = "id",foreignKey=@ForeignKey(name = "warehouse-stock_allocation_fk")
            ))
    private Warehouse warehouse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinTable(
            name = "product_variant_has_stock_allocations",
            joinColumns = @JoinColumn(name = "media_asset_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "product_variant_id", referencedColumnName = "id",foreignKey=@ForeignKey(name = "stock_allocations_variant_fk")
            ))
    private Variant variant;

    @Column(name = "quantity", nullable = false)
    private int quantity = 0;

    @Column(name = "reserved_quantity", nullable = false)
    private int reservedQuantity = 0;

    @Version
    private Long version;

    // Business methods
    @Column(name = "allocated_quantity", nullable = false)
    private int allocatedQuantity = 0;

    @Column(name = "in_transit_quantity", nullable = false)
    private int inTransitQuantity = 0;

    @Column(name = "safety_stock", nullable = false)
    private int safetyStock = 0;

    @Column(name = "reorder_point", nullable = false)
    private int reorderPoint = 0;

    @Column(name = "max_stock_level")
    private Integer maxStockLevel;

    @Enumerated(EnumType.STRING)
    @Column(name = "stock_status", nullable = false)
    private StockStatus stockStatus = StockStatus.ACTIVE;

    // Regional stock allocation (for multi-region inventory)
    @Column(name = "region_code", length = 5)
    private String regionCode; // ZA, NG, KE, etc.

    @Column(name = "is_primary_allocation")
    private Boolean isPrimaryAllocation = false;

    // Business methods
    public int getAvailableQuantity() {
        return quantity - reservedQuantity - allocatedQuantity;
    }

    public int getNetQuantity() {
        return quantity - reservedQuantity;
    }

    public void reserve(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Reserve amount must be positive");
        }
        if (amount > getAvailableQuantity()) {
            throw new InsufficientStockException(
                    String.format("Not enough available stock. Requested: %d, Available: %d",
                            amount, getAvailableQuantity()));
        }
        reservedQuantity += amount;
        updateStockStatus();
    }

    public void allocate(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Allocate amount must be positive");
        }
        if (amount > getAvailableQuantity()) {
            throw new InsufficientStockException(
                    String.format("Not enough available stock for allocation. Requested: %d, Available: %d",
                            amount, getAvailableQuantity()));
        }
        allocatedQuantity += amount;
        updateStockStatus();
    }

    public void release(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Release amount must be positive");
        }
        if (amount > reservedQuantity) {
            throw new IllegalArgumentException(
                    String.format("Cannot release more than reserved. Requested: %d, Reserved: %d",
                            amount, reservedQuantity));
        }
        reservedQuantity -= amount;
        updateStockStatus();
    }

    public void deallocate(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Deallocate amount must be positive");
        }
        if (amount > allocatedQuantity) {
            throw new IllegalArgumentException(
                    String.format("Cannot deallocate more than allocated. Requested: %d, Allocated: %d",
                            amount, allocatedQuantity));
        }
        allocatedQuantity -= amount;
        updateStockStatus();
    }

    public void adjustQuantity(int newQuantity) {
        if (newQuantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        if (newQuantity < (reservedQuantity + allocatedQuantity)) {
            throw new IllegalArgumentException(
                    String.format("New quantity cannot be less than reserved + allocated. New: %d, Reserved+Allocated: %d",
                            newQuantity, reservedQuantity + allocatedQuantity));
        }
        this.quantity = newQuantity;
        updateStockStatus();
    }

    public void receiveStock(int receivedQuantity) {
        if (receivedQuantity <= 0) {
            throw new IllegalArgumentException("Received quantity must be positive");
        }
        this.quantity += receivedQuantity;
        updateStockStatus();
    }

    public void shipStock(int shippedQuantity) {
        if (shippedQuantity <= 0) {
            throw new IllegalArgumentException("Shipped quantity must be positive");
        }
        if (shippedQuantity > getNetQuantity()) {
            throw new InsufficientStockException(
                    String.format("Not enough stock to ship. Requested: %d, Net Quantity: %d",
                            shippedQuantity, getNetQuantity()));
        }
        this.quantity -= shippedQuantity;
        updateStockStatus();
    }

    private void updateStockStatus() {
        if (quantity <= 0) {
            stockStatus = StockStatus.OUT_OF_STOCK;
        } else if (quantity <= safetyStock) {
            stockStatus = StockStatus.LOW_STOCK;
        } else if (maxStockLevel != null && quantity >= maxStockLevel) {
            stockStatus = StockStatus.OVERSTOCKED;
        } else if (quantity <= reorderPoint) {
            stockStatus = StockStatus.REORDER_NEEDED;
        } else {
            stockStatus = StockStatus.ACTIVE;
        }
    }

    public boolean needsReorder() {
        return quantity <= reorderPoint && stockStatus == StockStatus.REORDER_NEEDED;
    }

    public int calculateReorderQuantity() {
        if (maxStockLevel == null) {
            return reorderPoint - quantity + safetyStock;
        }
        return Math.max(reorderPoint - quantity + safetyStock, maxStockLevel - quantity);
    }

    // SA-Specific business method for regional compliance
    public boolean isCompliantForRegion(String targetRegionCode) {
        if (regionCode == null || targetRegionCode == null) {
            return true; // No regional restrictions
        }
        return regionCode.equals(targetRegionCode) || isPrimaryAllocation;
    }
}
