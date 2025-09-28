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
@Table(name = "provider_fees")
@NoArgsConstructor
@AllArgsConstructor
public class ProviderFees {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id")
    private PaymentProvider provider;

    @Column(name = "fixed_fee", nullable = false)
    private Double fixedFee;

    @Column(name = "percent_fee", nullable = false)
    private Double percentFee;

    @Column(name = "min_amount", nullable = false)
    private Double minAmount;

    @Column(name = "max_amount", nullable = false)
    private Double maxAmount;

    @Column(name = "currency", length = 3, nullable = false)
    private String currency;
}
