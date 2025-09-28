package za.co.pms.model.settings.currency;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * @author NMMkhungo
 * @since 2025/09/28
 **/
@Getter
@Setter
@Entity
@Table(name = "advanced_features")
public class AdvancedFeatures {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "real_time_conversion")
    private Boolean realTimeConversion;

    @Column(name = "historical_rates")
    private Boolean historicalRates;

    @Column(name = "multi_currency_wallets")
    private Boolean multiCurrencyWallets;

    @Column(name = "auto_currency_switching")
    private Boolean autoCurrencySwitching;

    @Column(name = "offline_mode")
    private Boolean offlineMode;

    @Column(name = "rate_alerts")
    private Boolean rateAlerts;

    @Column(name = "predictive_rates")
    private Boolean predictiveRates;

    @Column(name = "batch_processing")
    private Boolean batchProcessing;

    @Column(name = "api_rate_limits")
    private Integer apiRateLimits;
}