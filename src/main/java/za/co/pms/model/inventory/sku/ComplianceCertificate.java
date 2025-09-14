package za.co.pms.model.inventory.sku;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import za.co.pms.model.Vendor;

/**
 * @author NMMkhungo
 * @since 2025/09/14
 **/

@Getter
@Setter
@Entity
public class ComplianceCertificate {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false)
    private Long id;

    @OneToOne( cascade = CascadeType.ALL)
    @JoinTable(
            name = "vendor_has_compliance_certificate",
            joinColumns = @JoinColumn(name = "compliance_certificate_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "vendor_id", referencedColumnName = "id",foreignKey=@ForeignKey(name = "vendor_compliance_certificate_fk")
            ))
    private Vendor vendor;
}
