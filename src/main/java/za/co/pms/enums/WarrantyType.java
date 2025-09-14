package za.co.pms.enums;

import lombok.Getter;

/**
 * @author NMMkhungo
 * @since 2025/09/14
 **/
@Getter
public enum WarrantyType {
    STANDARD("Standard manufacturer warranty"),
    EXTENDED("Extended warranty"),
    PREMIUM("Premium warranty with additional coverage"),
    SUPPLIER("Supplier warranty"),
    CUSTOM("Custom warranty");

    private final String description;

    WarrantyType(String description) {
        this.description = description;
    }
}