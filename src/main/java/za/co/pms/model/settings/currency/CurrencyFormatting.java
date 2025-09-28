package za.co.pms.model.settings.currency;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import za.co.pms.model.settings.Currency;

/**
 * @author NMMkhungo
 * @since 2025/09/28
 **/
@Getter
@Setter
@Entity
@Table(name = "currency_formatting")
public class CurrencyFormatting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "currency_code")
    private Currency currency;

    @Column(name = "thousands_separator")
    private String thousandsSeparator;

    @Column(name = "decimal_separator")
    private String decimalSeparator;

    @Column(name = "symbol_position")
    private String symbolPosition;

    @Column(name = "space_after_symbol")
    private Boolean spaceAfterSymbol;

    @Column(name = "negative_format")
    private String negativeFormat;
}
