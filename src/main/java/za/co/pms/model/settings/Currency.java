package za.co.pms.model.settings;

import jakarta.persistence.*;
import lombok.*;
import za.co.pms.enums.Region;
import za.co.pms.enums.CurrencyStatus;
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

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CurrencyStatus status;

    @Column(name = "priority")
    private Integer priority;

    @Enumerated(EnumType.STRING)
    @Column(name = "region" , nullable = false)
    private Region region;

    @Column(name = "central_bank")
    private String centralBank;

    @Column(name = "digital_maturity")
    private String digitalMaturity;

    @OneToOne(mappedBy = "currency", cascade = CascadeType.ALL)
    private CurrencyFormatting formatting;

    @OneToOne(mappedBy = "currency", cascade = CascadeType.ALL)
    private CurrencyFxRules fxRules;

    @ManyToMany(mappedBy = "currencies")
    @ToString.Exclude
    private Set<RegionalHierarchy> regionalHierarchies = new HashSet<>();

}