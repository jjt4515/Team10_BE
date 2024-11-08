package poomasi.domain.order._aftersales.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import poomasi.domain.order._aftersales.entity._product.ProductAfterSales;

public interface ProductAfterSalesRepository extends JpaRepository<ProductAfterSales, Long> {
}
