package poomasi.domain.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import poomasi.domain.order.entity.OrderedProduct;

public interface OrderedProductRepository extends JpaRepository<OrderedProduct, Long> {
}
