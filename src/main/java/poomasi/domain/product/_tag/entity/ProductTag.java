package poomasi.domain.product._tag.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class ProductTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    Long productId;

    @Enumerated(EnumType.STRING)
    ProductTagEnum productTagEnum;

    @Builder
    public ProductTag(Long productId, ProductTagEnum productTagEnum) {
        this.productId = productId;
        this.productTagEnum = productTagEnum;
    }
}
