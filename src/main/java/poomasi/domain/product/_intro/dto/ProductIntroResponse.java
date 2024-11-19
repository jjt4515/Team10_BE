package poomasi.domain.product._intro.dto;

import lombok.Builder;
import poomasi.domain.image.dto.response.ImageResponse;
import poomasi.domain.product._intro.entity.ProductIntro;

@Builder
public record ProductIntroResponse(
        Long productIntroId,

        String mainTitle,
        ImageResponse mainImage,

        String subTitle1,
        String subDesc1,
        ImageResponse subImage1,

        String subTitle2,
        String subDesc2,
        ImageResponse subImage2,

        String subTitle3,
        String subDesc3,
        ImageResponse subImage3
) {

    public static ProductIntroResponse fromEntity(ProductIntro product) {
        return ProductIntroResponse.builder()
                .productIntroId(product.getId())

                .mainImage(ImageResponse.fromEntity(product.getMainImage()))
                .mainTitle(product.getMainTitle())

                .subTitle1(product.getSubTitle1())
                .subDesc1(product.getSubDesc1())
                .subImage1(ImageResponse.fromEntity(product.getSubImage1()))

                .subTitle2(product.getSubTitle2())
                .subDesc2(product.getSubDesc2())
                .subImage2(ImageResponse.fromEntity(product.getSubImage2()))

                .subTitle3(product.getSubTitle3())
                .subDesc3(product.getSubDesc3())
                .subImage3(ImageResponse.fromEntity(product.getSubImage3()))
                .build();
    }
}
