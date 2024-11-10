package poomasi.domain.product._intro.dto;

import lombok.Builder;
import poomasi.domain.product._intro.entity.ProductIntro;
import poomasi.domain.product.entity.Product;

@Builder
public record ProductIntroResponse(
        String mainTitle,
        String mainImage,

        String subTitle1,
        String subDesc1,
        String subImage1,

        String subTitle2,
        String subDesc2,
        String subImage2,

        String subTitle3,
        String subDesc3,
        String subImage3
) {
    public static ProductIntroResponse fromEntity(ProductIntro product){
        return ProductIntroResponse.builder()
                .mainImage(product.getMainImage())
                .mainTitle(product.getMainTitle())

                .subTitle1(product.getSubTitle1())
                .subDesc1(product.getSubDesc1())
                .subImage1(product.getSubImage1())

                .subTitle2(product.getSubTitle2())
                .subDesc2(product.getSubDesc2())
                .subImage2(product.getSubImage2())

                .subTitle3(product.getSubTitle3())
                .subDesc3(product.getSubDesc3())
                .subImage3(product.getSubImage3())
                .build();
    }
}
