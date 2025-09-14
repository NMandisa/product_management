package za.co.pms.model.product;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "sku")
public class Sku {
    @Id
    @Column(nullable = false)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;

    private String barcode;
    private String type; // e.g., "internal", "manufacturer"

    @OneToOne(mappedBy = "sku")
    private Variant productVariant;
}