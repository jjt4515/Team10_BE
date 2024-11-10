package poomasi.domain.product.dto;

import java.util.List;
import lombok.Builder;
import poomasi.domain.product._intro.dto.ProductIntroResponse;
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
        String storeName,
        List<String> tags,
        ProductIntroResponse productIntro
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
                .storeName(product.getStore().getName())
                .categoryId(product.getCategoryId())
                .tags(tags)
                .productIntro(ProductIntroResponse.fromEntity(product.getProductIntro()))
                .build();
    }
}
