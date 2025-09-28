package za.co.pms.model.settings.currency;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author NMMkhungo
 * @since 2025/09/28
 **/
@Getter
@Setter
@Entity
@Table(name = "circuit_breaker_config")
@NoArgsConstructor
@AllArgsConstructor
public class CircuitBreakerConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "failure_threshold", nullable = false)
    private Integer failureThreshold;

    @Column(name = "reset_timeout", nullable = false)
    private Long resetTimeout;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "fx_configuration_id")
    private FxConfiguration fxConfiguration;
}