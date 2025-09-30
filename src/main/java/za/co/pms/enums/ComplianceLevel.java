package za.co.pms.enums;

import lombok.Getter;

/**
 * @author NMMkhungo
 * @since 2025/09/14
 **/
@Getter
public enum ComplianceLevel {
    NON_COMPLIANT("Non-Compliant", "High risk of penalties", "RED"),
    LOW("Low Compliance", "Moderate risk, requires improvement", "ORANGE"),
    MEDIUM("Medium Compliance", "Meets basic requirements", "YELLOW"),
    HIGH("High Compliance", "Exceeds requirements", "GREEN"),
    EXCELLENT("Excellent Compliance", "Industry leader", "BLUE"),
    UNKNOWN("Unknown", "Not yet assessed", "GRAY");

    private final String description;
    private final String riskAssessment;
    private final String colorCode;

    ComplianceLevel(String description, String riskAssessment, String colorCode) {
        this.description = description;
        this.riskAssessment = riskAssessment;
        this.colorCode = colorCode;
    }
}