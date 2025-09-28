package za.co.pms.model.settings.currency;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

/**
 * @author NMMkhungo
 * @since 2025/09/28
 **/
@Getter
@Setter
@Entity
@Table(name = "fx_configuration")
public class FxConfiguration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "primary_source", nullable = false)
    private String primarySource;

    @Column(name = "markup", nullable = false)
    private Double markup;

    @Column(name = "cache_timeout_minutes", nullable = false)
    private Integer cacheTimeoutMinutes;

    @Column(name = "retry_attempts", nullable = false)
    private Integer retryAttempts;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "circuit_breaker_id")
    private CircuitBreakerConfig circuitBreaker;

    @OneToMany(mappedBy = "fxConfiguration", cascade = CascadeType.ALL)
    private Set<FxBackupSource> backupSources = new HashSet<>();

    public void addCircuitBreakerConfig(CircuitBreakerConfig circuitBreaker){
        circuitBreaker.setFxConfiguration(this);
    }
    public void removeOCircuitBreakerConfig(CircuitBreakerConfig circuitBreaker){
        circuitBreaker.setFxConfiguration(null);
    }

}
