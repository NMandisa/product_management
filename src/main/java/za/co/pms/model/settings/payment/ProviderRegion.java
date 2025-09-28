package za.co.pms.model.settings.payment;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.pms.model.settings.PaymentProvider;

/**
 * @author NMMkhungo
 * @since 2025/09/28
 **/
@Setter
@Getter
@Entity
@Table(name = "provider_region")
@NoArgsConstructor
@AllArgsConstructor
public class ProviderRegion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id")
    private PaymentProvider provider;

    @Column(name = "region_code", length = 10, nullable = false)
    private String regionCode; // ZA, NG, KE, etc.

    public ProviderRegion(PaymentProvider provider, String regionCode) {
        this.provider = provider;
        this.regionCode = regionCode;
    }
}
