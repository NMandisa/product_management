package za.co.pms.model.settings;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.pms.model.settings.payment.*;

import java.util.HashSet;
import java.util.Set;

/**
 * @author NMMkhungo
 * @since 2025/09/28
 **/
@Setter
@Getter
@Entity
@Table(name = "payment_provider")
@NoArgsConstructor
public class PaymentProvider {
    @Id
    @Column(name = "provider_id")
    private String id; // pp_snapscan, pp_paystack, etc.

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "type", nullable = false)
    private String type; // mobile_wallet, gateway, card_processor

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "version", nullable = false)
    private String version;

    @OneToOne(mappedBy = "provider", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private ProviderSettlement settlement;

    @OneToOne(mappedBy = "provider", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private ProviderFees fees;

    @OneToOne(mappedBy = "provider", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private ProviderIntegration integration;

    @OneToMany(mappedBy = "provider", cascade = CascadeType.ALL)
    private Set<ProviderRegion> regions = new HashSet<>();

    @OneToMany(mappedBy = "provider", cascade = CascadeType.ALL)
    private Set<ProviderCurrency> currencies = new HashSet<>();

    @OneToMany(mappedBy = "provider", cascade = CascadeType.ALL)
    private Set<ProviderCapability> capabilities = new HashSet<>();

    @OneToMany(mappedBy = "provider", cascade = CascadeType.ALL)
    private Set<ProviderMetadata> metadata = new HashSet<>();
}
