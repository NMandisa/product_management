package za.co.pms.model.product;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import za.co.pms.model.Promotion;

import java.time.LocalDateTime;

/**
 * @author NMMkhungo
 * @since 2025/09/14
 **/
@Entity
@Table(name = "price_changes")
@Getter
@Setter
public class PriceChange {
    @Id
    @Column(nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promotion_id", foreignKey = @ForeignKey(name = "fk_price_change_promotion"))
    private Promotion promotion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "old_price_id", foreignKey = @ForeignKey(name = "fk_price_change_old_price"))
    private Price oldPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "new_price_id", foreignKey = @ForeignKey(name = "fk_price_change_new_price"))
    private Price newPrice;

    @Column(nullable = false)
    private LocalDateTime changedAt;

    // SA-Specific Audit Fields
    @CreatedDate
    private LocalDateTime createdAt;

}