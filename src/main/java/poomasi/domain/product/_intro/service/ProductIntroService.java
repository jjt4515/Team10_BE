package poomasi.domain.product._intro.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poomasi.domain.image.entity.Image;
import poomasi.domain.image.repository.ImageRepository;
import poomasi.domain.member.entity.Member;
import poomasi.domain.product._intro.dto.ProductIntroUpdateRequest;
import poomasi.domain.product._intro.dto.ProductIntroResponse;
import poomasi.domain.product._intro.repository.ProductIntroRepository;
import poomasi.domain.product.entity.Product;
import poomasi.domain.product.repository.ProductRepository;
import poomasi.global.error.BusinessError;
import poomasi.global.error.BusinessException;

@Service
@RequiredArgsConstructor
public class ProductIntroService {

    private final ProductIntroRepository productIntroRepository;
    private final ProductRepository productRepository;
    private final ImageRepository imageRepository;

    public ProductIntroResponse getIntro(Long productId) {
        Product product = getProduct(productId);
        return ProductIntroResponse.fromEntity(product.getProductIntro());
    }

    @Transactional
    public void updateIntro(Member member, ProductIntroUpdateRequest productIntroUpdateRequest, Long productId) {
        Product product = getProduct(productId);
        if (!member.getId().equals(product.getFarmerId())) {
            throw new BusinessException(BusinessError.MEMBER_ID_MISMATCH);
        }

        Image mainImage = getImage(productIntroUpdateRequest.mainImageId());
        Image subImage1 = getImage(productIntroUpdateRequest.subImage1Id());
        Image subImage2 = getImage(productIntroUpdateRequest.subImage2Id());
        Image subImage3 = getImage(productIntroUpdateRequest.subImage3Id());

        product.getProductIntro().update(productIntroUpdateRequest,mainImage,subImage1,subImage2,subImage3);
    }

    private Product getProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(BusinessError.PRODUCT_NOT_FOUND));
    }

    private Image getImage(Long imageId) {
        if(imageId == null)
            return null;
        return imageRepository.findById(imageId)
                .orElseThrow(() -> new BusinessException(BusinessError.IMAGE_NOT_FOUND));
    }
}
