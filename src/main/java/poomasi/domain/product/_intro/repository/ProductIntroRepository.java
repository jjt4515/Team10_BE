package poomasi.domain.product._intro.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import poomasi.domain.product._intro.entity.ProductIntro;

import java.util.Optional;

@Repository
public interface ProductIntroRepository extends JpaRepository<ProductIntro, Long> {

    Optional<ProductIntro> findByProductId(Long productId);
}
