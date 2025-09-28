package za.co.pms.model.settings.localization;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

/**
 * @author NMMkhungo
 * @since 2025/09/28
 **/
@Builder
@Getter
@Setter
@Entity
@Table(name = "regional_settings")
@AllArgsConstructor
@NoArgsConstructor
public class RegionalSettings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "region_group", unique = true)
    private String regionGroup; // southernAfrica, westAfrica, etc.

    @Column(name = "default_language", length = 10)
    private String defaultLanguage;

    @Column(name = "date_format")
    private String dateFormat;

    @Column(name = "time_format")
    private String timeFormat;

    @Column(name = "first_day_of_week")
    private Integer firstDayOfWeek;

    @OneToMany(mappedBy = "regionalSettings", cascade = CascadeType.ALL)
    private Set<RegionalFallbackLanguage> fallbackLanguages = new HashSet<>();
}
