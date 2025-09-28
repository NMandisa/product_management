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
@Table(name = "allowed_currency_cross")
@NoArgsConstructor
@AllArgsConstructor
public class AllowedCurrencyCross {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fx_rules_id")
    private CurrencyFxRules fxRules;

    @Column(name = "allowed_currency", length = 3)
    private String allowedCurrency;

    public AllowedCurrencyCross(CurrencyFxRules fxRules, String allowedCurrency) {
        this.fxRules = fxRules;
        this.allowedCurrency = allowedCurrency;
    }
}