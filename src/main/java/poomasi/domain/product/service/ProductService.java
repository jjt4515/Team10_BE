package poomasi.domain.product.service;

import jdk.jfr.Description;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import poomasi.domain.order.entity.Order;
import poomasi.domain.order.entity.OrderedProduct;
import poomasi.domain.order.repository.OrderRepository;
import poomasi.domain.product.dto.ProductResponse;
import poomasi.domain.product.entity.Product;
import poomasi.domain.product.repository.ProductRepository;
import poomasi.global.error.BusinessError;
import poomasi.global.error.BusinessException;

import java.util.List;

import static poomasi.global.error.BusinessError.PRODUCT_STOCK_QUANTITY_EXCEEDED;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    public List<ProductResponse> getAllProducts() {
        return productRepository.findAllByDeletedAtIsNull()
                .stream()
                .map(ProductResponse::fromEntity)
                .toList();
    }

    public ProductResponse getProductByProductId(Long productId) {
        return ProductResponse.fromEntity(findProductById(productId));
    }

    public Product findValidProductById(Long productId) {
        Product product = findProductById(productId);
        if (product.getStock() == 0) {
            throw new BusinessException(BusinessError.PRODUCT_STOCK_ZERO);
        }
        return product;
    }

    public Product findProductById(Long productId) {
        return productRepository.findByIdAndDeletedAtIsNull(productId)
                .orElseThrow(() -> new BusinessException(BusinessError.PRODUCT_NOT_FOUND));
    }

    @Transactional
    public void saveExistedProduct(Product product) {
        productRepository.save(product);
    }

    @Description("재고 차감 메서드. 감소하다 exception이 일어나면 rollback하고 결제 취소 해야 함")
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void decreaseStock(Order order) {
        List<OrderedProduct> orderedProductList = order.getOrderedProducts();
        for (OrderedProduct orderedProduct : orderedProductList) {
            Product product = orderedProduct.getProduct();
            Integer remainQuantity = product.getStock(); //남은 수량
            Integer subtractQuantity = orderedProduct.getCount();//빼야 할 수량
            if (subtractQuantity > remainQuantity) {
                throw new BusinessException(PRODUCT_STOCK_QUANTITY_EXCEEDED);
            }
            product.subtractStock(subtractQuantity);
        }
    }
}
