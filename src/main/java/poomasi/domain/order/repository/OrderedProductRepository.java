package poomasi.domain.order.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import poomasi.domain.order.entity.OrderedProduct;

import java.util.List;

@Repository
public interface OrderedProductRepository extends JpaRepository<OrderedProduct, Long> {

    @Query("SELECT op FROM OrderedProduct op WHERE op.product.store.id = :storeId")
    List<OrderedProduct> findByStoreId(@Param("storeId") Long storeId);

}
