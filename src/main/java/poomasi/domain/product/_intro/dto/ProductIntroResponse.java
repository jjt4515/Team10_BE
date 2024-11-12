package poomasi.domain.product._intro.dto;

import lombok.Builder;
import poomasi.domain.product._intro.entity.ProductIntro;

@Builder
public record ProductIntroResponse(
        Long productIntroId,

        String mainTitle,
        String mainImageUrl,

        String subTitle1,
        String subDesc1,
        String subImage1Url,

        String subTitle2,
        String subDesc2,
        String subImage2Url,

        String subTitle3,
        String subDesc3,
        String subImage3Url
) {

    public static ProductIntroResponse fromEntity(ProductIntro product) {
        return ProductIntroResponse.builder()
                .productIntroId(product.getId())

                .mainImageUrl(product.getMainImage().getImageUrl())
                .mainTitle(product.getMainTitle())

                .subTitle1(product.getSubTitle1())
                .subDesc1(product.getSubDesc1())
                .subImage1Url(product.getSubImage1().getImageUrl())

                .subTitle2(product.getSubTitle2())
                .subDesc2(product.getSubDesc2())
                .subImage2Url(product.getSubImage2().getImageUrl())

                .subTitle3(product.getSubTitle3())
                .subDesc3(product.getSubDesc3())
                .subImage3Url(product.getSubImage3().getImageUrl())
                .build();
    }
}
