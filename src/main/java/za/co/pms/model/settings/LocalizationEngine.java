package za.co.pms.model.settings;

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
@Table(name = "localization_engine")
@AllArgsConstructor
@NoArgsConstructor
public class LocalizationEngine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "auto_detection", nullable = false)
    private Boolean autoDetection;

    @Column(name = "fallback_chains", nullable = false)
    private Boolean fallbackChains;

    @Column(name = "context_aware", nullable = false)
    private Boolean contextAware;

    @Column(name = "version", nullable = false)
    private String version;
}
