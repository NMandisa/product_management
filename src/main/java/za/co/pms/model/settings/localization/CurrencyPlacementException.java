package za.co.pms.model.settings.localization;

import jakarta.persistence.*;
import lombok.*;

/**
 * @author NMMkhungo
 * @since 2025/09/28
 **/
@Builder
@Getter
@Setter
@Entity
@Table(name = "currency_placement_exception")
@AllArgsConstructor
@NoArgsConstructor
public class CurrencyPlacementException {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "language_code", length = 10)
    private String languageCode; // fr, ar, ja

    @Column(name = "placement", nullable = false)
    private String placement; // after, beforeNoSpace

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "currency_placement_id")
    private CurrencyPlacementConfig currencyPlacementConfig;

    public CurrencyPlacementException(String languageCode, String placement, CurrencyPlacementConfig currencyPlacementConfig) {
        this.languageCode=languageCode;
        this.placement=placement;
        this.currencyPlacementConfig=currencyPlacementConfig;
    }
}
