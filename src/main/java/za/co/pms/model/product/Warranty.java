package za.co.pms.model.product;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import za.co.pms.enums.WarrantyType;

import java.util.HashSet;
import java.util.Set;

/**
 * @author NMMkhungo
 * @since 2025/09/14
 **/
@Getter
@Setter
@Entity
public class Warranty {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WarrantyType type = WarrantyType.STANDARD;

    // Warranty coverage details
    @ElementCollection
    @CollectionTable(name = "warranty_coverage", joinColumns = @JoinColumn(name = "warranty_id"))
    @Column(name = "coverage_item")
    private Set<String> coverageItems = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "warranty_exclusions", joinColumns = @JoinColumn(name = "warranty_id"))
    @Column(name = "exclusion")
    private Set<String> exclusions = new HashSet<>();

}
