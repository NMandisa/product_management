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
@Table(name = "accessibility_settings")
@AllArgsConstructor
@NoArgsConstructor
public class AccessibilitySettings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "high_contrast", nullable = false)
    private Boolean highContrast;

    @Column(name = "large_text", nullable = false)
    private Boolean largeText;

    @Column(name = "screen_reader", nullable = false)
    private Boolean screenReader;

    @Column(name = "voice_commands", nullable = false)
    private Boolean voiceCommands;
}