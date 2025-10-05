package poomasi.domain.product.repository;

import java.util.Collection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import poomasi.domain.product.entity.Product;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByIdAndDeletedAtIsNull(Long id);
    List<Product> findAllByDeletedAtIsNull();

    @Query("select p from Product p where p.stock=0 and p.farmerId = :id")
    List<Product> findSoldOut(Long id);
}
