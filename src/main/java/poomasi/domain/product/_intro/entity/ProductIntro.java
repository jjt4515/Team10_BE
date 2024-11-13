package poomasi.domain.product._intro.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import poomasi.domain.image.entity.Image;
import poomasi.domain.product._intro.dto.ProductIntroUpdateRequest;
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
    @Setter
    private Product product;

    private String mainTitle;
    @OneToOne(cascade = CascadeType.ALL)
    private Image mainImage;

    private String subTitle1;
    private String subDesc1;
    @OneToOne(cascade = CascadeType.ALL)
    private Image subImage1;

    private String subTitle2;
    private String subDesc2;
    @OneToOne(cascade = CascadeType.ALL)
    private Image subImage2;

    private String subTitle3;
    private String subDesc3;
    @OneToOne(cascade = CascadeType.ALL)
    private Image subImage3;

    @Builder
    public ProductIntro(Product product, String mainTitle, Image mainImage, String subTitle1,
            String subDesc1, Image subImage1, String subTitle2, String subDesc2, Image subImage2,
            String subTitle3, String subDesc3, Image subImage3) {
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

    public void update(ProductIntroUpdateRequest productIntroUpdateRequest) {
        this.mainTitle = productIntroUpdateRequest.mainTitle();

        this.subTitle1 = productIntroUpdateRequest.subTitle1();
        this.subDesc1 = productIntroUpdateRequest.subDesc1();

        this.subTitle2 = productIntroUpdateRequest.subTitle2();
        this.subDesc2 = productIntroUpdateRequest.subDesc2();

        this.subTitle3 = productIntroUpdateRequest.subTitle3();
        this.subDesc3 = productIntroUpdateRequest.subDesc3();
    }

    public void updateImage(Image mainImage, Image subImage1, Image subImage2, Image subImage3){
        this.mainImage = mainImage;
        this.subImage1 = subImage1;
        this.subImage2 = subImage2;
        this.subImage3 = subImage3;
    }
}
