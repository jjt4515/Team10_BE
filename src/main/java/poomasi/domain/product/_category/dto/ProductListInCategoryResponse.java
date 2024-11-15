package poomasi.domain.product._category.dto;

import lombok.Builder;
import poomasi.domain.product.entity.Product;

import java.math.BigDecimal;

@Builder
public record ProductListInCategoryResponse(
        Long categoryId,
        String name,
        String description,
        String imageUrl,
        Integer quantity,
        BigDecimal price
) {

    public static ProductListInCategoryResponse fromEntity(Product product) {
        return ProductListInCategoryResponse.builder()
                .categoryId(product.getCategoryId())
                .name(product.getName())
                .description(product.getDescription())
                .imageUrl(product.getImageUrl())
                .quantity(product.getStock())
                .price(product.getPrice())
                .build();
    }
}
