package poomasi.domain.product._intro.dto;

import poomasi.domain.product._intro.entity.ProductIntro;
import poomasi.domain.product.entity.Product;

public record ProductIntroUpdateRequest(
        String mainTitle,

        String subTitle1,
        String subDesc1,

        String subTitle2,
        String subDesc2,

        String subTitle3,
        String subDesc3
) {

    public ProductIntro toEntity(Product product) {
        return ProductIntro.builder()
                .product(product)

                .mainTitle(mainTitle)
                //.mainImage(mainImage)

                .subTitle1(subTitle1)
                .subDesc1(subDesc1)
                //.subImage1(subImage1)

                .subTitle2(subTitle2)
                .subDesc2(subDesc2)
                //.subImage2(subImage2)

                .subTitle3(subTitle3)
                .subDesc3(subDesc3)
                //.subImage3(subImage3)
                .build();
    }
}
