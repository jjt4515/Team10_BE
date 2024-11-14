package poomasi.domain.aftersales.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import poomasi.domain.aftersales.entity._product.ProductAfterSalesDetail;

@Repository
public interface ProductAfterSalesDetailRepository extends JpaRepository<ProductAfterSalesDetail, Long> {


}
