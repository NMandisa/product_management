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
@Table(name = "provider_settlement")
@NoArgsConstructor
@AllArgsConstructor
public class ProviderSettlement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id")
    private PaymentProvider provider;

    @Column(name = "settlement_time", nullable = false)
    private String settlementTime; // T+0, T+1, T+2

    @Column(name = "settlement_method", nullable = false)
    private String settlementMethod;

    @Column(name = "success_rate", nullable = false)
    private Double successRate;

    @Column(name = "max_amount", nullable = false)
    private Double maxAmount;
}
