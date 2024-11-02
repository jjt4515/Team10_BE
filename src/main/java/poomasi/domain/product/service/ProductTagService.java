package poomasi.domain.product.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poomasi.domain.product.dto.ProductTagRequest;
import poomasi.domain.product.entity.Product;
import poomasi.domain.product.entity.ProductTagEnum;
import poomasi.domain.product.repository.ProductRepository;
import poomasi.global.error.BusinessError;
import poomasi.global.error.BusinessException;

@Service
@RequiredArgsConstructor
public class ProductTagService {

    private final ProductRepository productRepository;

    @Transactional
    public void addTag(ProductTagRequest productTagRequest) {
        Product product = CheckProduct(productTagRequest);
        ProductTagEnum productTagEnum = checkEnum(productTagRequest);

        product.getTags().add(productTagEnum);
    }

    private Product CheckProduct(ProductTagRequest productTagRequest) {
        return productRepository.findById(productTagRequest.productId())
                .orElseThrow(() -> new BusinessException(BusinessError.PRODUCT_NOT_FOUND));
    }

    @Transactional
    public void deleteTag(ProductTagRequest productTagRequest) {
        Product product = CheckProduct(productTagRequest);
        ProductTagEnum productTagEnum = checkEnum(productTagRequest);

        product.getTags().remove(productTagEnum);
    }

    private ProductTagEnum checkEnum(ProductTagRequest productTagRequest) {
        ProductTagEnum productTagEnum = null;
        try {
            productTagEnum = ProductTagEnum.valueOf(productTagRequest.tagEnum());
        } catch (IllegalArgumentException e) {
            throw new BusinessException(BusinessError.INVALID_TAG_NAME);
        }
        return productTagEnum;
    }
}
