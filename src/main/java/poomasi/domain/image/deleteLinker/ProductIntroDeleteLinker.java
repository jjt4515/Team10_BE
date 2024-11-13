package poomasi.domain.image.deleteLinker;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import poomasi.domain.image.entity.Image;
import poomasi.domain.image.entity.ImageType;
import poomasi.domain.product._intro.entity.ProductIntro;
import poomasi.domain.product._intro.service.ProductIntroService;

import java.util.Arrays;
import java.util.List;

@Component
public class ProductIntroDeleteLinker implements ImageDeleteLinker {

    private final ProductIntroService productIntroService;

    public ProductIntroDeleteLinker(ProductIntroService productIntroService) {
        this.productIntroService = productIntroService;
    }

    @Override
    public boolean supports(ImageType type) {
        return type == ImageType.PRODUCT_INTRO;
    }

    @Transactional
    @Override
    public void handleImageDeletion(Image image) {
        ProductIntro productIntro = productIntroService.getIntroByIntroId(image.getReferenceId());

        List<Image> images = Arrays.asList(productIntro.getMainImage(), productIntro.getSubImage1(), productIntro.getSubImage2(), productIntro.getSubImage3());
        for (int i = 0; i < images.size(); i++) {
            if (images.get(i) == image) {
                switch (i) {
                    case 0 -> productIntro.setMainImage(null);
                    case 1 -> productIntro.setSubImage1(null);
                    case 2 -> productIntro.setSubImage2(null);
                    case 3 -> productIntro.setSubImage3(null);
                }
            }
        }
        productIntroService.saveExistedProductIntro(productIntro);
    }
}