package poomasi.domain.image.linker;

import org.springframework.stereotype.Service;
import poomasi.domain.image.entity.Image;
import poomasi.domain.image.entity.ImageType;
import poomasi.domain.product._intro.entity.ProductIntro;
import poomasi.domain.product._intro.service.ProductIntroService;
import poomasi.global.error.BusinessException;

import java.util.Arrays;
import java.util.List;

import static poomasi.global.error.BusinessError.IMAGE_LIMIT_EXCEED;

@Service
public class ProductIntroImageLinker implements ImageLinker {

    private final ProductIntroService productIntroService;

    public ProductIntroImageLinker(ProductIntroService productIntroService) {
        this.productIntroService = productIntroService;
    }

    @Override
    public boolean supports(ImageType type) {
        return type == ImageType.PRODUCT_INTRO;
    }

    @Override
    public void link(Long referenceId, Image savedImage) {
        ProductIntro productIntro = productIntroService.getIntroByIntroId(referenceId);
        addImageToProductIntro(productIntro, savedImage);
        productIntroService.saveExistedProductIntro(productIntro);
    }

    private void addImageToProductIntro(ProductIntro productIntro, Image savedImage) {
        List<Image> images = Arrays.asList(
                productIntro.getMainImage(),
                productIntro.getSubImage1(),
                productIntro.getSubImage2(),
                productIntro.getSubImage3()
        );

        for (int i = 0; i < images.size(); i++) {
            if (images.get(i) == null) {
                setImageByIndex(productIntro, i, savedImage);
                return;
            }
        }

        throw new BusinessException(IMAGE_LIMIT_EXCEED);
    }

    private void setImageByIndex(ProductIntro productIntro, int index, Image image) {
        switch (index) {
            case 0 -> productIntro.setMainImage(image);
            case 1 -> productIntro.setSubImage1(image);
            case 2 -> productIntro.setSubImage2(image);
            case 3 -> productIntro.setSubImage3(image);
        }
    }
}