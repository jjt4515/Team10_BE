package poomasi.domain.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import poomasi.domain.order.entity._product.ProductOrderDetails;

@RequestMapping
public interface ProductOrderDetailsRepository extends JpaRepository<ProductOrderDetails,Long> {

}
