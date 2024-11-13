package poomasi.domain.farm._category.dto.response;

import lombok.Builder;
import poomasi.domain.farm._category.domain.FarmCategory;

@Builder
public record FarmCategoryResponse(
        Long id,
        String name,
        String imageUrl
) {
    public static FarmCategoryResponse toResponse(FarmCategory farmCategory) {
        return FarmCategoryResponse.builder()
                .id(farmCategory.getId())
                .name(farmCategory.getName())
                .imageUrl(farmCategory.getImageUrl())
                .build();
    }
}
