package poomasi.domain.product._intro.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poomasi.domain.auth.security.userdetail.UserDetailsImpl;
import poomasi.domain.member.entity.Member;
import poomasi.domain.product._intro.dto.ProductIntroRequest;
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

    public ProductIntroResponse getIntro(Long productId) {
        Product product = getProduct(productId);
        return ProductIntroResponse.fromEntity(product.getProductIntro());
    }

    @Transactional
    public void updateIntro(ProductIntroRequest productIntroRequest, Long productId) {
        Member member = getMember();
        Product product = getProduct(productId);
        if (!member.getId().equals(product.getFarmerId())) {
            throw new BusinessException(BusinessError.MEMBER_ID_MISMATCH);
        }

        product.getProductIntro().update(productIntroRequest);
    }

    private Member getMember() {
        Authentication authentication = SecurityContextHolder
                .getContext().getAuthentication();
        Object impl = authentication.getPrincipal();
        return ((UserDetailsImpl) impl).getMember();
    }

    private Product getProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(BusinessError.PRODUCT_NOT_FOUND));
    }
}
