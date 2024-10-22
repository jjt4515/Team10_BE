package poomasi.domain.product._tag.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poomasi.domain.product._tag.dto.TagRequest;
import poomasi.domain.product._tag.entity.ProductTag;
import poomasi.domain.product._tag.repository.TagRepository;
import poomasi.domain.product.entity.Product;
import poomasi.domain.product.repository.ProductRepository;
import poomasi.global.error.BusinessError;
import poomasi.global.error.BusinessException;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;
    private final ProductRepository productRepository;

    @Transactional
    public Long addTag(TagRequest tagRequest) {
        Product product = CheckProduct(tagRequest);
        ProductTag productTag = tagRequest.toEntity();
        productTag = tagRepository.save(productTag);
        product.getTags().add(productTag);
        return productTag.getId();
    }

    private Product CheckProduct(TagRequest tagRequest) {
        return productRepository.findById(tagRequest.productId())
                .orElseThrow(() -> new BusinessException(BusinessError.PRODUCT_NOT_FOUND));
    }

    @Transactional
    public void deleteTag(TagRequest tagRequest) {
        Product product = CheckProduct(tagRequest);
        ProductTag productTag = tagRepository.findByProductIdAndTagName(tagRequest.productId(),
                        tagRequest.tagEnum())
                .orElseThrow(() -> new BusinessException(BusinessError.TAG_NOT_FOUND));
        product.getTags().remove(productTag);
        tagRepository.delete(productTag);
    }
}
