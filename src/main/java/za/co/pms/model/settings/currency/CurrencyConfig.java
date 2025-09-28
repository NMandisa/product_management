package za.co.pms.model.settings.currency;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;
import za.co.pms.model.settings.Currency;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author NMMkhungo
 * @since 2025/09/28
 **/
@Getter
@Setter
@Entity
@Table(name = "currency_config")
@NoArgsConstructor
@AllArgsConstructor
public class CurrencyConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "default_currency", nullable = false)
    private Currency defaultCurrency; // ZAR


    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "fx_config_id")
    @ToString.Exclude
    private FxConfiguration fxConfiguration;

    @OneToMany(mappedBy = "config", cascade = CascadeType.ALL)
    @ToString.Exclude
    private Set<RegionalDefault> regionalDefaults = new HashSet<>();

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "advanced_features_id")
    @ToString.Exclude
    private AdvancedFeatures advancedFeatures;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        CurrencyConfig that = (CurrencyConfig) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
