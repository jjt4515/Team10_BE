package poomasi.domain.review.service.product;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poomasi.domain.member.entity.Member;
import poomasi.domain.order.entity.OrderedProduct;
import poomasi.domain.order.entity.OrderedProductStatus;
import poomasi.domain.order.repository.OrderedProductRepository;
import poomasi.domain.product.entity.Product;
import poomasi.domain.product.service.ProductService;
import poomasi.domain.review.dto.ReviewRequest;
import poomasi.domain.review.dto.ReviewResponse;
import poomasi.domain.review.entity.EntityType;
import poomasi.domain.review.entity.Review;
import poomasi.domain.review.repository.ReviewRepository;
import poomasi.global.error.BusinessError;
import poomasi.global.error.BusinessException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductService productService;
    private final OrderedProductRepository orderedProductRepository;

    public List<ReviewResponse> getProductReview(Long productId) {
        Product product = getProduct(productId);
        return product.getReviewList().stream().map(ReviewResponse::fromEntity).toList();
    }

    @Transactional
    public Long registerProductReview(Member member, Long orderedProductId, ReviewRequest reviewRequest) {
        // s3 이미지 저장하고 주소 받아와서 review에 추가해주기
        OrderedProduct orderedProduct = findOrderedProduct(orderedProductId);

        if(orderedProduct.getReview() != null)
            throw new BusinessException(BusinessError.REVIEW_ALREADY_EXIST);

        checkOrderStatus(orderedProduct);

        Review pReview = reviewRequest.toEntity(orderedProduct.getProduct().getId(), EntityType.PRODUCT, member);
        pReview = reviewRepository.save(pReview);
        orderedProduct.setReview(pReview);
        orderedProduct.getProduct().addReview(pReview);
        return pReview.getId();
    }

    private void checkOrderStatus(OrderedProduct orderedProduct) {
        if( orderedProduct.getOrderedProductStatus() == OrderedProductStatus.DELIVERED)
            throw new BusinessException(BusinessError.ORDER_NOT_DELIVERED);
    }

    private OrderedProduct findOrderedProduct(Long orderedProductId){
        return orderedProductRepository.findById(orderedProductId)
                .orElseThrow(()->new BusinessException(BusinessError.ORDERED_PRODUCT_NOT_FOUND));
    }

    private Product getProduct(Long productId){
        return productService.findProductById(productId);
    }
}


