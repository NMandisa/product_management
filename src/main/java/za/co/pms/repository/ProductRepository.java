package za.co.pms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import za.co.pms.model.Product;

/**
 * @author NMMkhungo
 * @since 2025/09/13
 **/
@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {
}
