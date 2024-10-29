package poomasi.domain.product.dto;

import java.util.List;
import lombok.Builder;
import poomasi.domain.product.entity.Product;
import poomasi.domain.product.entity.ProductTagEnum;

@Builder
public record ProductResponse(
        Long id,
        String name,
        Long price,
        Integer stock,
        String description,
        String imageUrl,
        Long categoryId,
        List<String> tags
) {

    public static ProductResponse fromEntity(Product product) {
       List<String> tags = product.getTags().stream().map(ProductTagEnum::getKoreanName).toList();

        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .stock(product.getStock())
                .description(product.getDescription())
                .imageUrl(product.getImageUrl())
                .categoryId(product.getCategoryId())
                .tags(tags)
                .build();
    }
}
