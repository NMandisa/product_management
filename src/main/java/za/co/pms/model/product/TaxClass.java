package za.co.pms.model.product;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import za.co.pms.enums.TaxType;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author NMMkhungo
 * @since 2025/09/14
 **/
@Getter
@Setter
@Entity
public class TaxClass {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false)
    private Long id;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private TaxType taxType;

    @NotBlank
    @Size(max = 255)
    private String name;

    @Size(max = 1000)
    private String description;

    // Rate as percentage (e.g., 15 for 15%)
    @DecimalMin("0.0")
    @DecimalMax("100.0")
    private BigDecimal rate;

    @Column(nullable = false)
    private boolean active = true;

    // SA-Specific fields
    @Pattern(regexp = "VAT\\d{3}")
    private String sarsCode;

    // Business method to calculate tax amount
    public BigDecimal calculateTax(BigDecimal amount) {
        if (taxType == TaxType.EXEMPT || taxType == TaxType.ZERO_RATED) {
            return BigDecimal.ZERO;
        }
        return amount.multiply(rate.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP));
    }
}
