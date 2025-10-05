package poomasi.domain.product._intro.dto;

import poomasi.domain.product._intro.entity.ProductIntro;
import poomasi.domain.product.entity.Product;

public record ProductIntroUpdateRequest(
        String mainTitle,

        String subTitle1,
        String subDesc1,
        String imageUrl1,

        String subTitle2,
        String subDesc2,
        String imageUrl2,

        String subTitle3,
        String subDesc3,
        String imageUrl3
) {

}
