package poomasi.domain.product._tag.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import poomasi.domain.product._tag.entity.ProductTag;

@Repository
public interface TagRepository extends JpaRepository<ProductTag, Long> {

    @Query(value = "select * from product_tag t where t.product_id=:productId and t.product_tag_enum = :tagEnum", nativeQuery = true)
    Optional<ProductTag> findByProductIdAndTagName(Long productId, String tagEnum);
}
