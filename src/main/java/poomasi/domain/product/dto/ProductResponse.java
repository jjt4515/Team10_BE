package poomasi.domain.product.dto;

import lombok.Builder;
import poomasi.domain.image.dto.response.ImageResponse;
import poomasi.domain.image.entity.Image;
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
        List<ImageResponse> images,
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
        List<ImageResponse> images = product.getImages().stream().map(ImageResponse::fromEntity).toList();

        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .stock(product.getStock())
                .images(images)
                .storeName(product.getStore().getName())
                .categoryId(product.getCategoryId())
                .growEnv(product.getGrowEnv())
                .shippingFee(product.getShippingFee())
                .description(product.getDescription())
                .orderLimit(product.getOrderLimit())
                .tags(tags)
                .productIntro(ProductIntroResponse.fromEntity(product.getProductIntro()))
                .build();
    }
}
