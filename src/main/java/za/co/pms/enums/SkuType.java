package za.co.pms.enums;

/**
 * @author NMMkhungo
 * @since 2025/09/14
 **/
public enum SkuType {
    INTERNAL("Internal company SKU"),
    MANUFACTURER("Manufacturer SKU"),
    SUPPLIER("Supplier SKU"),
    CUSTOM("Custom SKU");

    private final String description;

    SkuType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}