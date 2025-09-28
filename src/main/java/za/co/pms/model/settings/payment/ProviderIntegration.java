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
@Table(name = "provider_integration")
@NoArgsConstructor
@AllArgsConstructor
public class ProviderIntegration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id")
    private PaymentProvider provider;

    @Column(name = "api_url")
    private String apiUrl;

    @Column(name = "auth_type", nullable = false)
    private String authType;

    @Column(name = "timeout_ms", nullable = false)
    private Integer timeoutMs;

    @Column(name = "rate_limit", nullable = false)
    private Integer rateLimit;

    @Column(name = "webhook_url")
    private String webhookUrl;
}
