package poomasi.domain.product.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poomasi.domain.image.entity.Image;
import poomasi.domain.image.repository.ImageRepository;
import poomasi.domain.member.entity.Member;
import poomasi.domain.product._category.entity.Category;
import poomasi.domain.product._category.repository.CategoryRepository;
import poomasi.domain.product._category.service.CategoryService;
import poomasi.domain.store.entity.Store;
import poomasi.domain.store.repository.StoreRepository;
import poomasi.domain.product.dto.ProductRegisterRequest;
import poomasi.domain.product.dto.UpdateProductQuantityRequest;
import poomasi.domain.product.entity.Product;
import poomasi.domain.product.repository.ProductRepository;
import poomasi.domain.store.entity.Store;
import poomasi.domain.store.repository.StoreRepository;
import poomasi.domain.store.service.StoreService;
import poomasi.global.error.BusinessError;
import poomasi.global.error.BusinessException;

@Service
@RequiredArgsConstructor
public class ProductFarmerService {

    private final ProductRepository productRepository;
    private final CategoryService categoryService;
    private final StoreService storeService;
    private final ImageRepository imageRepository;

    @Transactional
    public Long registerProduct(Member member, ProductRegisterRequest request) {
        Category category = getCategory(request.categoryId());
        Store store = member.getStore();

        Product saveProduct = productRepository.save(request.toEntity(member,store));

        category.addProduct(saveProduct);
        store.addProduct(saveProduct);
        saveProduct.getProductIntro().setProduct(saveProduct);
        return saveProduct.getId();
    }

    private Image getImage(Long imageId) {
        if(imageId == null)
            return null;
        return imageRepository.findById(imageId)
                .orElseThrow(() -> new BusinessException(BusinessError.IMAGE_NOT_FOUND));
    }

    @Transactional
    public void modifyProduct(Member member, ProductRegisterRequest productRequest,
            Long productId) {
        Product product = getProduct(productId);
        checkAuth(member, product);

        Long categoryId = product.getCategoryId();
        Category oldCategory = getCategory(categoryId);

        oldCategory.deleteProduct(product);//원래 카테고리에서 삭제
        product = productRepository.save(product.modify(productRequest)); //상품 갱신

        categoryId = productRequest.categoryId();
        Category newCategory = getCategory(categoryId);
        newCategory.addProduct(product);
    }

    @Transactional
    public void deleteProduct(Member member, Long productId) {
        Product product = getProduct(productId);
        checkAuth(member, product);

        Long categoryId = product.getCategoryId();
        Category category = getCategory(categoryId);

        category.deleteProduct(product);
        productRepository.delete(product);
    }

    @Transactional
    public void addQuantity(Member member, Long productId, UpdateProductQuantityRequest request) {
        Product product = getProduct(productId);
        checkAuth(member, product);
        product.addStock(request.quantity());
    }

    private Product getProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(()->new BusinessException(BusinessError.PRODUCT_NOT_FOUND));
    }

    private Category getCategory(Long categoryId) {
        return categoryService.getCategory(categoryId);
    }

    private void checkAuth(Member member, Product product) {
        if (!product.getFarmerId().equals(member.getId())) {
            throw new BusinessException(BusinessError.MEMBER_ID_MISMATCH);
        }
    }
}
