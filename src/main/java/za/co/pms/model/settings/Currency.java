package za.co.pms.model.settings;

import jakarta.persistence.*;
import lombok.*;
import za.co.pms.model.settings.currency.CurrencyFormatting;
import za.co.pms.model.settings.currency.CurrencyFxRules;
import za.co.pms.model.settings.currency.RegionalHierarchy;

import java.util.HashSet;
import java.util.Set;

/**
 * @author NMMkhungo
 * @since 2025/09/28
 **/
@Setter
@Getter
@Entity
@Table(name = "currency")
@AllArgsConstructor
@NoArgsConstructor
public class Currency {
    @Id
    @Column(name = "currency_code", length = 3)
    private String code; // ZAR, USD, etc.

    @Column(name = "symbol", nullable = false)
    private String symbol;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "precision", nullable = false)
    private Integer precision;

    @Column(name = "compliance_ref")
    private String complianceRef;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "priority")
    private Integer priority;

    @Column(name = "region")
    private String region;

    @Column(name = "central_bank")
    private String centralBank;

    @Column(name = "digital_maturity")
    private String digitalMaturity;

    @OneToOne(mappedBy = "currency", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private CurrencyFormatting formatting;

    @OneToOne(mappedBy = "currency", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private CurrencyFxRules fxRules;

    @ManyToMany(mappedBy = "currencies")
    @ToString.Exclude
    private Set<RegionalHierarchy> regionalHierarchies = new HashSet<>();

}