package za.co.pms.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * @author NMMkhungo
 * @since 2025/09/14
 **/
@Getter
@Setter
@Entity
public class MediaAsset {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false)
    private Long id;
    private String name;
    private String url;
    private String type; // IMAGE, VIDEO, DOCUMENT, etc.

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinTable(
            name = "product_has_media_assets",
            joinColumns = @JoinColumn(name = "media_asset_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "product_id", referencedColumnName = "id",foreignKey=@ForeignKey(name = "product_media_id_fk")
            ))
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinTable(
            name = "category_has_media_assets",
            joinColumns = @JoinColumn(name = "media_asset_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "category_id", referencedColumnName = "id",foreignKey=@ForeignKey(name = "category_media_asset_fk")
            ))
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinTable(
            name = "brand_has_media_assets",
            joinColumns = @JoinColumn(name = "media_asset_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "brand_id", referencedColumnName = "id",foreignKey=@ForeignKey(name = "brand_media_asset_fk")
            ))
    private Brand brand;

}
