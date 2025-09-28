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
@Table(name = "provider_capability")
@NoArgsConstructor
@AllArgsConstructor
public class ProviderCapability {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id")
    private PaymentProvider provider;

    @Column(name = "capability", nullable = false)
    private String capability; // qr, instant_refund, recurring, etc.

    public ProviderCapability(PaymentProvider provider, String capability) {
        this.provider = provider;
        this.capability = capability;
    }
}
