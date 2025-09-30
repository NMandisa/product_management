package za.co.pms.model.compliance;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.pms.model.settings.compliance.ComplianceRegion;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author NMMkhungo
 * @since 2025/09/30
 **/
@Getter
@Setter
@Entity
@Table(name = "reporting_schedules")
@AllArgsConstructor
@NoArgsConstructor
public class ReportingSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_code")
    private ComplianceRegion complianceRegion;

    @Column(name = "report_type", nullable = false)
    private String reportType; // VAT, INCOME_TAX, ANNUAL_RETURN

    @Column(name = "frequency", nullable = false)
    private String frequency; // MONTHLY, QUARTERLY, ANNUALLY

    @Column(name = "due_day_of_month")
    private Integer dueDayOfMonth;

    @Column(name = "penalty_amount", precision = 10, scale = 2)
    private BigDecimal penaltyAmount;

    @Column(name = "late_submission_days")
    private Integer lateSubmissionDays;

    public LocalDateTime calculateNextDueDate() {
        // Implementation for calculating next due date based on frequency
        return LocalDateTime.now().plusMonths(1); // Simplified
    }
}
