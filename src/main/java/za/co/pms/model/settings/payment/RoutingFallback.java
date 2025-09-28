package za.co.pms.model.settings.payment;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author NMMkhungo
 * @since 2025/09/28
 **/
@Setter
@Getter
@Entity
@Table(name = "routing_fallback")
@NoArgsConstructor
@AllArgsConstructor
public class RoutingFallback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fallback_type", nullable = false)
    private String fallbackType; // regionPreferred, globalGateway, manualReview

    @Column(name = "fallback_order", nullable = false)
    private Integer fallbackOrder;

    public RoutingFallback(String fallbackType, Integer fallbackOrder) {
        this.fallbackType = fallbackType;
        this.fallbackOrder = fallbackOrder;
    }
}
