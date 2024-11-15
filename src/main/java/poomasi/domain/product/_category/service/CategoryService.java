package poomasi.domain.product._category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import poomasi.domain.product._category.dto.CategoryResponse;
import poomasi.domain.product._category.dto.ProductListInCategoryResponse;
import poomasi.domain.product._category.entity.Category;
import poomasi.domain.product._category.repository.CategoryRepository;
import poomasi.global.error.BusinessError;
import poomasi.global.error.BusinessException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<CategoryResponse> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream()
                .map(CategoryResponse::fromEntity)
                .toList();
    }

    public List<ProductListInCategoryResponse> getProductInCategory(Long categoryId) {
        Category category = getCategory(categoryId);
        return category.getProducts()
                .stream()
                .map(ProductListInCategoryResponse::fromEntity)
                .toList();
    }

    public Category getCategory(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BusinessException(BusinessError.CATEGORY_NOT_FOUND));

    }

}
