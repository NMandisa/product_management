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
@Table(name = "tax_holidays")
@AllArgsConstructor
@NoArgsConstructor
public class TaxHoliday {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_code")
    private ComplianceRegion complianceRegion;

    @Column(name = "holiday_name", nullable = false)
    private String holidayName;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @Column(name = "tax_type", nullable = false)
    private String taxType; // VAT, INCOME_TAX, IMPORT_DUTY

    @Column(name = "reduction_percentage", precision = 5, scale = 2)
    private BigDecimal reductionPercentage;

    @Column(name = "eligibility_criteria")
    private String eligibilityCriteria; // JSON criteria

    public boolean isActive() {
        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(startDate) && now.isBefore(endDate);
    }
}
