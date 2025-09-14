package za.co.pms.enums;

/**
 * @author NMMkhungo
 * @since 2025/09/14
 **/
public enum RuleType {
    ELIGIBILITY,    // Determines if a variant is eligible for promotion
    RESTRICTION,    // Restrictions on promotion application
    COMPLIANCE,     // SA-specific compliance rules
    REGULATORY,     // Government regulations
    BUSINESS,       // Business rules
    TECHNICAL,       // Technical constraints
    SHIPPING,
    PAYMENT,
    INVENTORY,
    CUSTOMER
}