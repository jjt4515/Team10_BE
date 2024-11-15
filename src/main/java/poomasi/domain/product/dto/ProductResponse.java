package poomasi.domain.product.dto;

import lombok.Builder;
import poomasi.domain.product._intro.dto.ProductIntroResponse;
import poomasi.domain.product.entity.Product;
import poomasi.domain.product.entity.ProductTagEnum;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record ProductResponse(
        Long id,
        String name,
        BigDecimal price,
        Integer stock,
        String description,
        String imageUrl,
        Long categoryId,
        String storeName,
        List<String> tags,
        ProductIntroResponse productIntro,
        String growEnv,
        BigDecimal shippingFee
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
                .growEnv(product.getGrowEnv())
                .shippingFee(product.getShippingFee())
                .tags(tags)
                .productIntro(ProductIntroResponse.fromEntity(product.getProductIntro()))
                .build();
    }
}
