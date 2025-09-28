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
@Table(name = "provider_metadata")
@NoArgsConstructor
@AllArgsConstructor
public class ProviderMetadata {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id")
    private PaymentProvider provider;

    @Column(name = "metadata_key", nullable = false)
    private String key;

    @Column(name = "metadata_value")
    private String value;

    public ProviderMetadata(PaymentProvider provider, String key, String value) {
        this.provider = provider;
        this.key = key;
        this.value = value;
    }
}
