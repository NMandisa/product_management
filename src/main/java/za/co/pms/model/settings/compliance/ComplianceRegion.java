package za.co.pms.model.settings.compliance;

import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import za.co.pms.enums.ComplianceLevel;
import za.co.pms.enums.RegionCode;
import za.co.pms.model.compliance.Auditable;
import za.co.pms.model.compliance.ReportingSchedule;
import za.co.pms.model.compliance.TaxHoliday;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * @author NMMkhungo
 * @since 2025/09/28
 **/
@Getter
@Setter
@Entity
@Table(name = "compliance_region")
public class ComplianceRegion extends Auditable implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "region_code", length = 5)
    @NotNull(message = "Region code is required")
    private RegionCode regionCode;

    // Tax Information
    @Column(name = "tax_authority", nullable = false)
    @NotNull(message = "Tax authority is required")
    private String taxAuthority;

    @Column(name = "vat_rate", nullable = false, precision = 5, scale = 2)
    @Digits(integer = 3, fraction = 2, message = "VAT rate must have up to 3 integer and 2 fraction digits")
    private BigDecimal vatRate;

    @Column(name = "withholding_tax_rate", nullable = false, precision = 5, scale = 2)
    @Digits(integer = 3, fraction = 2, message = "Withholding tax rate must have up to 3 integer and 2 fraction digits")
    private BigDecimal withholdingTaxRate;

    @Column(name = "reporting_threshold", nullable = false, precision = 15, scale = 2)
    @Digits(integer = 13, fraction = 2, message = "Reporting threshold must have up to 13 integer and 2 fraction digits")
    private BigDecimal reportingThreshold;

    // SA-Specific Compliance Fields
    @Column(name = "sars_branch_code", length = 10)
    private String sarsBranchCode;

    @Column(name = "cpa_enforced", nullable = false)
    private Boolean cpaEnforced = false;

    @Column(name = "bbr_enforced", nullable = false)
    private Boolean bbrEnforced = false; // Broad-Based Black Economic Empowerment

    @Column(name = "import_duty_rate", precision = 5, scale = 2)
    @Digits(integer = 3, fraction = 2, message = "Import duty rate must have up to 3 integer and 2 fraction digits")
    private BigDecimal importDutyRate;

    // Regional Compliance Status
    @Enumerated(EnumType.STRING)
    @Column(name = "compliance_level", nullable = false)
    private ComplianceLevel complianceLevel = ComplianceLevel.UNKNOWN;

    @Column(name = "last_audit_date")
    private LocalDateTime lastAuditDate;

    @Column(name = "next_audit_due")
    private LocalDateTime nextAuditDue;

    @Column(name = "compliance_score", precision = 5, scale = 2)
    @Digits(integer = 3, fraction = 2, message = "Compliance score must have up to 3 integer and 2 fraction digits")
    private BigDecimal complianceScore;

    // Regional Specific Rules
    @Column(name = "requires_tax_invoice", nullable = false)
    private Boolean requiresTaxInvoice = true;

    @Column(name = "tax_invoice_format")
    private String taxInvoiceFormat; // JSON template or format spec

    @Column(name = "minimum_wage", precision = 10, scale = 2)
    @Digits(integer = 8, fraction = 2, message = "Minimum wage must have up to 8 integer and 2 fraction digits")
    private BigDecimal minimumWage;

    @Column(name = "environmental_levy", precision = 5, scale = 2)
    @Digits(integer = 3, fraction = 2, message = "Environmental levy must have up to 3 integer and 2 fraction digits")
    private BigDecimal environmentalLevy;

    // Additional Tax Fields
    @Column(name = "digital_services_tax_rate", precision = 5, scale = 2)
    @Digits(integer = 3, fraction = 2, message = "Digital services tax rate must have up to 3 integer and 2 fraction digits")
    private BigDecimal digitalServicesTaxRate;

    @Column(name = "turnover_tax_rate", precision = 5, scale = 2)
    @Digits(integer = 3, fraction = 2, message = "Turnover tax rate must have up to 3 integer and 2 fraction digits")
    private BigDecimal turnoverTaxRate;

    @Column(name = "corporate_tax_rate", precision = 5, scale = 2)
    @Digits(integer = 3, fraction = 2, message = "Corporate tax rate must have up to 3 integer and 2 fraction digits")
    private BigDecimal corporateTaxRate;

    // Compliance Tracking
    @Column(name = "tax_certificate_expiry")
    private LocalDateTime taxCertificateExpiry;

    @Column(name = "tcc_reference") // Tax Compliance Certificate
    private String tccReference;

    @Column(name = "tcc_valid", nullable = false)
    private Boolean tccValid = false;

    // Relationships
    @OneToMany(mappedBy = "complianceRegion", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ComplianceFlag> complianceFlags = new HashSet<>();

    @OneToMany(mappedBy = "complianceRegion", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RequiredLicense> requiredLicenses = new HashSet<>();

    @OneToOne(mappedBy = "complianceRegion", cascade = CascadeType.ALL)
    private AdditionalRules additionalRules;

    @OneToMany(mappedBy = "complianceRegion", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TaxHoliday> taxHolidays = new HashSet<>();

    @OneToMany(mappedBy = "complianceRegion", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ReportingSchedule> reportingSchedules = new HashSet<>();

    // Business Methods
    public boolean isVATApplicable() {
        return vatRate != null && vatRate.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean requiresWithholdingTax() {
        return withholdingTaxRate != null && withholdingTaxRate.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean hasDigitalServicesTax() {
        return digitalServicesTaxRate != null && digitalServicesTaxRate.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean isSAComplianceRegion() {
        return regionCode == RegionCode.ZA;
    }

    public boolean isSADCmember() {
        return regionCode != null && regionCode.isSADC();
    }

    public boolean isECOWASmember() {
        return regionCode != null && regionCode.isECOWAS();
    }

    public boolean isEACmember() {
        return regionCode != null && regionCode.isEAC();
    }

    public BigDecimal calculateVAT(BigDecimal amount) {
        if (!isVATApplicable() || amount == null) {
            return BigDecimal.ZERO;
        }
        return amount.multiply(vatRate).divide(new BigDecimal(100), 2, java.math.RoundingMode.HALF_UP);
    }

    public BigDecimal calculateWithholdingTax(BigDecimal amount) {
        if (!requiresWithholdingTax() || amount == null) {
            return BigDecimal.ZERO;
        }
        return amount.multiply(withholdingTaxRate).divide(new BigDecimal(100), 2, java.math.RoundingMode.HALF_UP);
    }

    public BigDecimal calculateDigitalServicesTax(BigDecimal amount) {
        if (!hasDigitalServicesTax() || amount == null) {
            return BigDecimal.ZERO;
        }
        return amount.multiply(digitalServicesTaxRate).divide(new BigDecimal(100), 2, java.math.RoundingMode.HALF_UP);
    }

    public boolean requiresReporting(BigDecimal annualRevenue) {
        return annualRevenue != null && annualRevenue.compareTo(reportingThreshold) >= 0;
    }

    public boolean isAuditDue() {
        return nextAuditDue != null && LocalDateTime.now().isAfter(nextAuditDue);
    }

    public boolean isTaxCertificateExpired() {
        return taxCertificateExpiry != null && LocalDateTime.now().isAfter(taxCertificateExpiry);
    }

    public boolean isFullyCompliant() {
        return complianceLevel == ComplianceLevel.HIGH &&
                Boolean.TRUE.equals(tccValid) &&
                !isTaxCertificateExpired() &&
                !isAuditDue();
    }

    public ComplianceLevel assessComplianceLevel() {
        if (complianceScore == null) return ComplianceLevel.UNKNOWN;

        if (complianceScore.compareTo(new BigDecimal("90")) >= 0) {
            return ComplianceLevel.HIGH;
        } else if (complianceScore.compareTo(new BigDecimal("70")) >= 0) {
            return ComplianceLevel.MEDIUM;
        } else if (complianceScore.compareTo(new BigDecimal("50")) >= 0) {
            return ComplianceLevel.LOW;
        } else {
            return ComplianceLevel.NON_COMPLIANT;
        }
    }

    public void updateComplianceScore(BigDecimal newScore) {
        this.complianceScore = newScore;
        this.complianceLevel = assessComplianceLevel();
    }

    // Factory methods for common regions
    public static ComplianceRegion createSouthAfrica() {
        ComplianceRegion za = new ComplianceRegion();
        za.setRegionCode(RegionCode.ZA);
        za.setTaxAuthority("South African Revenue Service (SARS)");
        za.setVatRate(new BigDecimal("15.00"));
        za.setWithholdingTaxRate(new BigDecimal("15.00"));
        za.setReportingThreshold(new BigDecimal("1000000.00")); // R1 million
        za.setSarsBranchCode("SARS001");
        za.setCpaEnforced(true);
        za.setBbrEnforced(true);
        za.setImportDutyRate(new BigDecimal("5.00"));
        za.setComplianceLevel(ComplianceLevel.HIGH);
        za.setRequiresTaxInvoice(true);
        za.setTaxInvoiceFormat("SARS-VAT-INVOICE");
        za.setMinimumWage(new BigDecimal("25.42")); // 2024 minimum wage
        za.setEnvironmentalLevy(new BigDecimal("1.00"));
        za.setDigitalServicesTaxRate(new BigDecimal("15.00"));
        za.setCorporateTaxRate(new BigDecimal("27.00"));
        za.setTccValid(true);
        return za;
    }

    public static ComplianceRegion createNigeria() {
        ComplianceRegion ng = new ComplianceRegion();
        ng.setRegionCode(RegionCode.NG);
        ng.setTaxAuthority("Federal Inland Revenue Service (FIRS)");
        ng.setVatRate(new BigDecimal("7.50"));
        ng.setWithholdingTaxRate(new BigDecimal("10.00"));
        ng.setReportingThreshold(new BigDecimal("25000000.00")); // ₦25 million
        ng.setCpaEnforced(false);
        ng.setBbrEnforced(false);
        ng.setImportDutyRate(new BigDecimal("10.00"));
        ng.setComplianceLevel(ComplianceLevel.MEDIUM);
        ng.setRequiresTaxInvoice(true);
        ng.setMinimumWage(new BigDecimal("30000.00")); // Monthly
        ng.setDigitalServicesTaxRate(new BigDecimal("6.00"));
        ng.setCorporateTaxRate(new BigDecimal("30.00"));
        return ng;
    }

    public static ComplianceRegion createKenya() {
        ComplianceRegion ke = new ComplianceRegion();
        ke.setRegionCode(RegionCode.KE);
        ke.setTaxAuthority("Kenya Revenue Authority (KRA)");
        ke.setVatRate(new BigDecimal("16.00"));
        ke.setWithholdingTaxRate(new BigDecimal("5.00"));
        ke.setReportingThreshold(new BigDecimal("5000000.00")); // KSh 5 million
        ke.setCpaEnforced(true);
        ke.setBbrEnforced(false);
        ke.setImportDutyRate(new BigDecimal("8.00"));
        ke.setComplianceLevel(ComplianceLevel.MEDIUM);
        ke.setRequiresTaxInvoice(true);
        ke.setMinimumWage(new BigDecimal("15000.00")); // Monthly
        ke.setDigitalServicesTaxRate(new BigDecimal("1.50"));
        ke.setCorporateTaxRate(new BigDecimal("30.00"));
        return ke;
    }

    // Convenience methods
    public void scheduleNextAudit(int monthsFromNow) {
        this.lastAuditDate = LocalDateTime.now();
        this.nextAuditDue = lastAuditDate.plusMonths(monthsFromNow);
    }

    public void renewTaxCertificate(int validityMonths) {
        this.taxCertificateExpiry = LocalDateTime.now().plusMonths(validityMonths);
        this.tccValid = true;
    }

    @PrePersist
    @PreUpdate
    private void validateComplianceData() {
        if (vatRate != null && vatRate.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("VAT rate cannot be negative");
        }
        if (withholdingTaxRate != null && withholdingTaxRate.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Withholding tax rate cannot be negative");
        }
        if (reportingThreshold != null && reportingThreshold.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Reporting threshold cannot be negative");
        }
    }
}
