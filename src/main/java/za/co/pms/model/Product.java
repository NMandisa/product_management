package za.co.pms.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import za.co.pms.model.product.Variant;

import java.util.ArrayList;
import java.util.List;

/**
 * @author NMMkhungo
 * @since 2025/09/13
 **/
@Getter
@Setter
@Entity
public class Product {
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

    @OneToOne( cascade = CascadeType.ALL)
    @JoinTable(
            name = "vendor_has_products",
            joinColumns = @JoinColumn(name = "product_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "vendor_id", referencedColumnName = "id",foreignKey=@ForeignKey(name = "vendor_product_fk")
            ))
    private Vendor vendor;

    private String name;
    private String description;

    // Helper methods
    public void addVariant(Variant variant) {
        variants.add(variant);
        variant.setProduct(this);
    }

    public void removeVariant(Variant variant) {
        variants.remove(variant);
        variant.setProduct(null);
    }

    // Business methods
    public int getTotalStock() {
        return variants.stream()
                .mapToInt(Variant::getTotalStock)
                .sum();
    }


}
