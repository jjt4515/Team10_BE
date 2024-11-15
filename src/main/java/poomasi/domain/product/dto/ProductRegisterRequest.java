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

        String subTitle1,
        String subDesc1,

        String subTitle2,
        String subDesc2,

        String subTitle3,
        String subDesc3,

        String oneLineDescription,
        Integer orderLimit

) {

    public Product toEntity(Member member, Store store) {
        ProductIntro productIntro = ProductIntro.builder()
                .mainTitle(mainTitle)

                .subTitle1(subTitle1)
                .subDesc1(subDesc1)

                .subTitle2(subTitle2)
                .subDesc2(subDesc2)

                .subTitle3(subTitle3)
                .subDesc3(subDesc3)
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
                .oneLineDescription(oneLineDescription)
                .orderLimit(orderLimit)
                .build();
    }
}
