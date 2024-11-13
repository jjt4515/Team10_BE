package poomasi.domain.product._intro.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poomasi.domain.member.entity.Member;
import poomasi.domain.product._intro.dto.ProductIntroUpdateRequest;
import poomasi.domain.product._intro.dto.ProductIntroResponse;
import poomasi.domain.product._intro.entity.ProductIntro;
import poomasi.domain.product._intro.repository.ProductIntroRepository;
import poomasi.domain.product.entity.Product;
import poomasi.domain.product.service.ProductService;
import poomasi.global.error.BusinessError;
import poomasi.global.error.BusinessException;

@Service
@RequiredArgsConstructor
public class ProductIntroService {

    private final ProductService productService;
    private final ProductIntroRepository productIntroRepository;

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

//        Image mainImage = getImage(productIntroUpdateRequest.mainImageId());
//        Image subImage1 = getImage(productIntroUpdateRequest.subImage1Id());
//        Image subImage2 = getImage(productIntroUpdateRequest.subImage2Id());
//        Image subImage3 = getImage(productIntroUpdateRequest.subImage3Id());

        product.getProductIntro().update(productIntroUpdateRequest);
    }

    private Product getProduct(Long productId) {
        return productService.findProductById(productId);
    }

    public ProductIntro getIntroByIntroId(Long productIntroId) {
        return productIntroRepository.findById(productIntroId)
                .orElseThrow(() -> new BusinessException(BusinessError.INTRO_NOT_FOUND));
    }

    @Transactional
    public void saveExistedProductIntro(ProductIntro productIntro){
        productIntroRepository.save(productIntro);
    }
}
