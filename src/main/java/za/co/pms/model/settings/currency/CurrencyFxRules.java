package za.co.pms.model.settings.currency;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import za.co.pms.model.settings.Currency;

import java.util.HashSet;
import java.util.Set;

/**
 * @author NMMkhungo
 * @since 2025/09/28
 **/
@Getter
@Setter
@Entity
@Table(name = "currency_fx_rules")
public class CurrencyFxRules {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "currency_code")
    private Currency currency;

    @Column(name = "default_rate_source")
    private String defaultRateSource;

    @Column(name = "cross_border_restrictions")
    private Boolean crossBorderRestrictions;

    @Column(name = "max_daily_limit")
    private Double maxDailyLimit;

    @Column(name = "volatility_threshold")
    private Double volatilityThreshold;

    @OneToMany(mappedBy = "fxRules", cascade = CascadeType.ALL)
    private Set<AllowedCurrencyCross> allowedCrosses = new HashSet<>();
}
