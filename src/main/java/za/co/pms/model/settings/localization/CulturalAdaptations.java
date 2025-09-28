package za.co.pms.model.settings.localization;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
@Table(name = "cultural_adaptations")
@AllArgsConstructor
@NoArgsConstructor
public class CulturalAdaptations {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "local_holidays", nullable = false)
    private Boolean localHolidays;

    @OneToMany(mappedBy = "culturalAdaptations", cascade = CascadeType.ALL)
    private Set<SupportedCalendar> supportedCalendars = new HashSet<>();

    @OneToMany(mappedBy = "culturalAdaptations", cascade = CascadeType.ALL)
    private Set<SupportedNumberSystem> supportedNumberSystems = new HashSet<>();

    @OneToOne(mappedBy = "culturalAdaptations", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private CurrencyPlacementConfig currencyPlacement;

    @OneToMany(mappedBy = "culturalAdaptations", cascade = CascadeType.ALL)
    private Set<DateFormatConfig> dateFormats = new HashSet<>();

    @OneToMany(mappedBy = "culturalAdaptations", cascade = CascadeType.ALL)
    private Set<TimeFormatConfig> timeFormats = new HashSet<>();

    @OneToMany(mappedBy = "culturalAdaptations", cascade = CascadeType.ALL)
    private Set<NumberFormatConfig> numberFormats = new HashSet<>();

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "localization_config_id")
    private LocalizationConfig localizationConfig;

}