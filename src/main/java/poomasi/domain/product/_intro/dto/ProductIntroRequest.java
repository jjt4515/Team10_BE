package poomasi.domain.product._intro.dto;

import poomasi.domain.product._intro.entity.ProductIntro;
import poomasi.domain.product.entity.Product;

public record ProductIntroRequest(
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

    public ProductIntro toEntity(Product product) {
        return ProductIntro.builder()
                .product(product)

                .mainTitle(mainTitle)
                .mainImage(mainImage)

                .subTitle1(subTitle1)
                .subDesc1(subDesc1)
                .subImage1(subImage1)

                .subTitle2(subTitle2)
                .subDesc2(subDesc2)
                .subImage2(subImage2)

                .subTitle3(subTitle3)
                .subDesc3(subDesc3)
                .subImage3(subImage3)
                .build();
    }
}
