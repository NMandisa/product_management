package za.co.pms.model.inventory.sku;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import za.co.pms.exception.InsufficientStockException;
import za.co.pms.model.product.Variant;

import java.time.LocalDateTime;

/**
 * @author NMMkhungo
 * @since 2025/09/14
 **/

@Getter
@Setter
@Entity
public class StockAllocation {
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
    private Variant productVariant;

    private int quantity;
    private int reservedQuantity;

    @Version
    private Long version;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    // Business methods
    public int getAvailableQuantity() {
        return quantity - reservedQuantity;
    }

    public void reserve(int amount) {
        if (amount > getAvailableQuantity()) {
            throw new InsufficientStockException("Not enough available stock");
        }
        reservedQuantity += amount;
    }

    public void release(int amount) {
        if (amount > reservedQuantity) {
            throw new IllegalArgumentException("Cannot release more than reserved");
        }
        reservedQuantity -= amount;
    }

    public void adjustQuantity(int newQuantity) {
        if (newQuantity < reservedQuantity) {
            throw new IllegalArgumentException("New quantity cannot be less than reserved quantity");
        }
        this.quantity = newQuantity;
    }
}
