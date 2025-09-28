package za.co.pms.model.settings.localization;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

/**
 * @author NMMkhungo
 * @since 2025/09/28
 **/
@Builder
@Entity
@Table(name = "regional_fallback_language")
@NoArgsConstructor
@AllArgsConstructor
public class RegionalFallbackLanguage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "language_code", length = 10)
    private String languageCode;

    @Column(name = "fallback_order")
    private Integer fallbackOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "regional_settings_id")
    private RegionalSettings regionalSettings;

    public RegionalFallbackLanguage(String languageCode, Integer fallbackOrder, RegionalSettings regionalSettings) {
        this.languageCode = languageCode;
        this.fallbackOrder = fallbackOrder;
        this.regionalSettings = regionalSettings;
    }
}
