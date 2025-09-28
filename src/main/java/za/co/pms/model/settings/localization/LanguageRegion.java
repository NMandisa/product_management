package za.co.pms.model.settings.localization;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author NMMkhungo
 * @since 2025/09/28
 **/
@Setter
@Getter
@Entity
@Table(name = "language_region")
@AllArgsConstructor
@NoArgsConstructor
public class LanguageRegion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "language_code")
    private Language language;

    @Column(name = "region_code", length = 50)
    private String regionCode; // ZA, NG, KE, etc.

    public LanguageRegion(Language language, String regionCode) {
        this.language = language;
        this.regionCode = regionCode;
    }
}
