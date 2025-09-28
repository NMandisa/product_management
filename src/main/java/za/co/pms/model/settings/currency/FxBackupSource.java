package za.co.pms.model.settings.currency;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * @author NMMkhungo
 * @since 2025/09/28
 **/
@Getter
@Setter
@Entity
@Table(name = "fx_backup_sources",
        uniqueConstraints = @UniqueConstraint(columnNames = {"fx_config_id", "source_name"}))
public class FxBackupSource {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "source_name", nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fx_config_id")
    private FxConfiguration fxConfiguration;

    public FxBackupSource(String name, FxConfiguration fxConfiguration) {
        this.name = name;
        this.fxConfiguration = fxConfiguration;
    }
}
