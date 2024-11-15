package poomasi.domain.product.dto;

import java.math.BigDecimal;
import java.util.List;
import lombok.Builder;
import poomasi.domain.image.entity.Image;
import poomasi.domain.product._intro.dto.ProductIntroResponse;
import poomasi.domain.product.entity.Product;
import poomasi.domain.product.entity.ProductTagEnum;

@Builder
public record ProductResponse(
        Long id,
        String name,
        BigDecimal price,
        Integer stock,
        String description,
        List<Image> images,
        Long categoryId,
        String storeName,
        List<String> tags,
        ProductIntroResponse productIntro,
        String growEnv,
        BigDecimal shippingFee,
        String oneLineDescription,
        Integer orderLimit
) {

    public static ProductResponse fromEntity(Product product) {
        List<String> tags = product.getTags().stream().map(ProductTagEnum::getKoreanName).toList();

        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .stock(product.getStock())
                .description(product.getDescription())
                .images(product.getImages())
                .storeName(product.getStore().getName())
                .categoryId(product.getCategoryId())
                .growEnv(product.getGrowEnv())
                .shippingFee(product.getShippingFee())
                .oneLineDescription(product.getOneLineDescription())
                .orderLimit(product.getOrderLimit())
                .tags(tags)
                .productIntro(ProductIntroResponse.fromEntity(product.getProductIntro()))
                .build();
    }
}
