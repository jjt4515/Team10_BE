package poomasi.domain.product._category.dto;

import java.math.BigDecimal;
import java.util.List;
import lombok.Builder;
import poomasi.domain.image.entity.Image;
import poomasi.domain.product.entity.Product;

@Builder
public record ProductListInCategoryResponse(
        Long categoryId,
        String name,
        String description,
        List<Image> images,
        Integer quantity,
        BigDecimal price
) {

    public static ProductListInCategoryResponse fromEntity(Product product) {
        return ProductListInCategoryResponse.builder()
                .categoryId(product.getCategoryId())
                .name(product.getName())
                .description(product.getDescription())
                .images(product.getImages())
                .quantity(product.getStock())
                .price(product.getPrice())
                .build();
    }
}
