package poomasi.domain.image.deleteLinker;

import org.springframework.stereotype.Component;
import poomasi.domain.image.entity.Image;
import poomasi.domain.image.entity.ImageType;
import poomasi.domain.product.entity.Product;
import poomasi.domain.product.service.ProductService;

@Component
public class ProductDeleteLinker implements ImageDeleteLinker {

    private final ProductService productService;

    public ProductDeleteLinker(ProductService productService) {
        this.productService = productService;
    }

    @Override
    public boolean supports(ImageType type) {
        return type == ImageType.PRODUCT;
    }

    @Override
    public void handleImageDeletion(Image image) {
        Product product = productService.findProductById(image.getReferenceId());
        product.getImages().remove(image);
        productService.saveExistedProduct(product);
    }
}
