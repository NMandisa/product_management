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
@Table(name = "provider_currency")
@NoArgsConstructor
@AllArgsConstructor
public class ProviderCurrency {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id")
    private PaymentProvider provider;

    @Column(name = "currency_code", length = 3, nullable = false)
    private String currencyCode;

    public ProviderCurrency(PaymentProvider provider, String currencyCode) {
        this.provider = provider;
        this.currencyCode = currencyCode;
    }
}
