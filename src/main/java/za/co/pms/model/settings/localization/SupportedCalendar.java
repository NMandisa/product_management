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
@Getter
@Setter
@Entity
@Table(name = "supported_calendar")
@AllArgsConstructor
@NoArgsConstructor
public class SupportedCalendar {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "calendar_name", nullable = false)
    private String calendarName; // gregorian, islamic, ethiopian

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cultural_adaptations_id")
    private CulturalAdaptations culturalAdaptations;

    public SupportedCalendar(String calendarName, CulturalAdaptations culturalAdaptations) {
        this.calendarName = calendarName;
        this.culturalAdaptations = culturalAdaptations;
    }
}
