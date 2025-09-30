package za.co.pms.model.settings.currency;

import jakarta.persistence.*;
import lombok.*;
import za.co.pms.model.settings.Currency;

import java.util.HashSet;
import java.util.Set;

/**
 * @author NMMkhungo
 * @since 2025/09/28
 **/
@Getter
@Setter
@Entity
@Table(name = "regional_hierarchy")
@NoArgsConstructor
@AllArgsConstructor
public class RegionalHierarchy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "region_name", unique = true)
    private String name;

    @ManyToMany
    @JoinTable(
            name = "regional_hierarchy_currency",
            joinColumns = @JoinColumn(name = "hierarchy_id"),
            inverseJoinColumns = @JoinColumn(name = "currency_code")
    )
    @ToString.Exclude
    private Set<Currency> currencies = new HashSet<>();

    public RegionalHierarchy(String regionName, Set<Currency> currencies) {
        this.name = regionName;
        this.currencies = currencies;
    }
}
