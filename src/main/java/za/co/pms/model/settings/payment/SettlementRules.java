package za.co.pms.model.settings.payment;

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
@Table(name = "settlement_rules")
@NoArgsConstructor
@AllArgsConstructor
public class SettlementRules {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "instant_settlement_max", nullable = false)
    private Double instantSettlementMax;

    @Column(name = "batch_processing_interval", nullable = false)
    private String batchProcessingInterval; // PT1H

    @Column(name = "reconciliation_window", nullable = false)
    private String reconciliationWindow; // PT2H

    @Column(name = "auto_retry_attempts", nullable = false)
    private Integer autoRetryAttempts;
}
