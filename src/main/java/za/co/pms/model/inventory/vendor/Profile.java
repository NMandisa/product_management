package za.co.pms.model.inventory.vendor;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import za.co.pms.model.Vendor;

import java.io.Serializable;

/**
 * @author NMMkhungo
 * @since 2025/09/14
 **/
@Getter
@Setter
@Entity
public class Profile implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false)
    private Long id;

    @OneToOne( cascade = CascadeType.ALL)
    @JoinTable(
            name = "vendor_has_profile",
            joinColumns = @JoinColumn(name = "profile_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "vendor_id", referencedColumnName = "id",foreignKey=@ForeignKey(name = "vendor_profile_fk")
            ))
    private Vendor vendor;

}
