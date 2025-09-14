package za.co.pms.model.product;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import za.co.pms.enums.SkuType;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "sku")
public class Sku {
    @Id
    @Column(nullable = false)
    private Long id;

    @NotBlank
    @Size(max = 50)
    @Column(unique = true, nullable = false)
    @EqualsAndHashCode.Include
    private String code;

    @Pattern(regexp = "\\d{8,14}") // Standard barcode formats
    private String barcode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SkuType type = SkuType.INTERNAL; // e.g., "internal", "manufacturer"

    @OneToOne(mappedBy = "sku")
    private Variant productVariant;

    // SA-Specific Fields
    @Pattern(regexp = "SARS\\d{9}") // SA Revenue Service code format
    private String sarsProductCode;

    private boolean vatApplicable = true; // Most products in SA have VAT

    // Audit fields
    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    // Business methods
    public boolean isManufacturerSku() {
        return SkuType.MANUFACTURER.equals(type);
    }

    public boolean isInternalSku() {
        return SkuType.INTERNAL.equals(type);
    }

    // Validation
    @PrePersist
    @PreUpdate
    private void validate() {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("SKU code cannot be null or empty");
        }

        if (barcode != null && !barcode.matches("\\d{8,14}")) {
            throw new IllegalArgumentException("Barcode must be 8-14 digits");
        }
    }
}