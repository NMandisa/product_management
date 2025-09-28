package za.co.pms.model.settings.payment;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author NMMkhungo
 * @since 2025/09/28
 **/
@Setter
@Getter
@Entity
@Table(name = "region_provider_mapping")
@NoArgsConstructor
@AllArgsConstructor
public class RegionProviderMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "region_group", nullable = false)
    private String regionGroup; // southAfrica, westAfrica, etc.

    @Column(name = "provider_id", nullable = false)
    private String providerId;

    @Column(name = "priority_order")
    private Integer priorityOrder;

    public RegionProviderMapping(String regionGroup, String providerId, Integer priorityOrder) {
        this.regionGroup = regionGroup;
        this.providerId = providerId;
        this.priorityOrder = priorityOrder;
    }
}
