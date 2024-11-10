package poomasi.domain.product._intro.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import poomasi.domain.product._intro.dto.ProductIntroRequest;
import poomasi.domain.product.entity.Product;

@Entity
@Getter
@NoArgsConstructor
public class ProductIntro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "id")
    private Product product;

    private String mainTitle;
    private String mainImage;

    private String subTitle1;
    private String subDesc1;
    private String subImage1;

    private String subTitle2;
    private String subDesc2;
    private String subImage2;

    private String subTitle3;
    private String subDesc3;
    private String subImage3;

    public ProductIntro(Product product) {
        this.product = product;
        this.mainTitle = "";
        this.mainImage = "";
        this.subTitle1 = "";
        this.subDesc1 = "";
        this.subImage1 = "";
        this.subTitle2 = "";
        this.subDesc2 = "";
        this.subImage2 = "";
        this.subTitle3 = "";
        this.subDesc3 = "";
        this.subImage3 = "";
    }

    @Builder
    public ProductIntro(Product product, String mainTitle, String mainImage, String subTitle1,
            String subDesc1, String subImage1, String subTitle2, String subDesc2, String subImage2,
            String subTitle3, String subDesc3, String subImage3) {
        this.product = product;
        this.mainTitle = mainTitle;
        this.mainImage = mainImage;
        this.subTitle1 = subTitle1;
        this.subDesc1 = subDesc1;
        this.subImage1 = subImage1;
        this.subTitle2 = subTitle2;
        this.subDesc2 = subDesc2;
        this.subImage2 = subImage2;
        this.subTitle3 = subTitle3;
        this.subDesc3 = subDesc3;
        this.subImage3 = subImage3;
    }

    public void update(ProductIntroRequest productIntroRequest) {
        this.mainTitle = productIntroRequest.mainTitle();
        this.mainImage = productIntroRequest.mainImage();
        this.subTitle1 = productIntroRequest.subTitle1();
        this.subDesc1 = productIntroRequest.subDesc1();
        this.subImage1 = productIntroRequest.subImage1();
        this.subTitle2 = productIntroRequest.subTitle2();
        this.subDesc2 = productIntroRequest.subDesc2();
        this.subImage2 = productIntroRequest.subImage2();
        this.subTitle3 = productIntroRequest.subTitle3();
        this.subDesc3 = productIntroRequest.subDesc3();
        this.subImage3 = productIntroRequest.subImage3();
    }
}
