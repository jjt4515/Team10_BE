package poomasi.domain.aftersales.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import poomasi.domain.aftersales.entity.ProductAfterSales;

@Repository
public interface ProductAfterSalesRepository extends JpaRepository<ProductAfterSales, Long> {
}
