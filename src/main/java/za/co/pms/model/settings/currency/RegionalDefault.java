package za.co.pms.model.settings.currency;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.pms.enums.CountryCode;

/**
 * @author NMMkhungo
 * @since 2025/09/28
 **/
@Getter
@Setter
@Entity
@Table(name = "regional_default")
@NoArgsConstructor
@AllArgsConstructor
public class RegionalDefault {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "country_code", length = 2)
    @Enumerated(EnumType.STRING)
    private CountryCode countryCode;

    @Column(name = "currency_code", length = 3)
    private String currencyCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "config_id")
    private CurrencyConfig config;

    public RegionalDefault(CountryCode countryCode, String currencyCode, FxConfiguration fxConfiguration) {
        this.countryCode = countryCode;
        this.currencyCode = currencyCode;
        this.config = null; // Will be set when CurrencyConfig is created
    }

}
