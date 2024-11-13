package poomasi.domain.product.dto;

import poomasi.domain.image.entity.Image;
import java.math.BigDecimal;
import org.hibernate.annotations.Comment;
import poomasi.domain.member.entity.Member;
import poomasi.domain.product._intro.entity.ProductIntro;
import poomasi.domain.store.entity.Store;
import poomasi.domain.product.entity.Product;
import poomasi.domain.store.entity.Store;

public record ProductRegisterRequest(
        //product
        Long categoryId,
        String name,
        String description,
        String imageUrl,
        Integer stock,
        BigDecimal price,
        @Comment("재배 환경")
        String growEnv,
        BigDecimal shippingFee,

        //product intro
        String mainTitle,
        //Long mainImageId,

        String subTitle1,
        String subDesc1,
        //Long subImage1Id,

        String subTitle2,
        String subDesc2,
        //Long subImage2Id,

        String subTitle3,
        String subDesc3
        //Long subImage3Id

) {

    public Product toEntity(Member member, Store store) {
        ProductIntro productIntro = ProductIntro.builder()
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

        return Product.builder()
                .categoryId(categoryId)
                .farmerId(member.getId())
                .name(name)
                .stock(stock)
                .description(description)
                .imageUrl(imageUrl)
                .stock(stock)
                .price(price)
                .store(store)
                .growEnv(growEnv)
                .shippingFee(shippingFee)
                .productIntro(productIntro)
                .build();
    }
}
