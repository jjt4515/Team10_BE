package poomasi.domain.product._tag.dto;

import poomasi.domain.product._tag.entity.ProductTag;
import poomasi.domain.product._tag.entity.ProductTagEnum;
import poomasi.global.error.BusinessError;
import poomasi.global.error.BusinessException;

public record TagRequest(
        Long productId,
        String tagEnum
) {

    public ProductTag toEntity() {
        ProductTagEnum productTagEnum = null;
        try {
            productTagEnum = ProductTagEnum.valueOf(tagEnum);
        } catch (IllegalArgumentException e) {
            throw new BusinessException(BusinessError.INVALID_TAG_NAME);
        }

        return ProductTag.builder()
                .productId(productId)
                .productTagEnum(productTagEnum)
                .build();

    }
}
