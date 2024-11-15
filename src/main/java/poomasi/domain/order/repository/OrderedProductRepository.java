package poomasi.domain.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import poomasi.domain.order.entity.OrderedProduct;

@Repository
public interface OrderedProductRepository extends JpaRepository<OrderedProduct, Long> {
}
