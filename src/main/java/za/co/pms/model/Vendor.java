package za.co.pms.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import za.co.pms.model.inventory.sku.ComplianceCertificate;
import za.co.pms.model.inventory.vendor.Profile;

import java.util.HashSet;
import java.util.Set;

/**
 * @author NMMkhungo
 * @since 2025/09/14
 **/
@Getter
@Setter
@Entity
public class Vendor {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false)
    private Long id;

    @OneToOne(mappedBy = "vendor")
    private Profile profile;

    @OneToMany(mappedBy = "vendor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Product> products = new HashSet<>();

    @OneToMany(mappedBy = "vendor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<ComplianceCertificate> certificates = new HashSet<>();

}
