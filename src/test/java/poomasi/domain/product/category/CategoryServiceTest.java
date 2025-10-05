package poomasi.domain.product.category;

import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import poomasi.domain.product._category.dto.CategoryResponse;
import poomasi.domain.product._category.dto.ProductListInCategoryResponse;
import poomasi.domain.product._category.entity.Category;
import poomasi.domain.product._category.repository.CategoryRepository;
import poomasi.domain.product._category.service.CategoryService;
import poomasi.domain.product.entity.Product;
import poomasi.global.error.BusinessError;
import poomasi.global.error.BusinessException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @InjectMocks
    private CategoryService categoryService;

    @Mock
    private CategoryRepository categoryRepository;

    @Nested
    @DisplayName("전체 카테고리 조회")
    class GetAllCategories {
        private List<Category> categories;

        @BeforeEach
        void setUp() {
            categories = Arrays.asList(
                    Category.builder().id(1L).name("카테고리1").build(),
                    Category.builder().id(2L).name("카테고리2").build(),
                    Category.builder().id(3L).name("카테고리3").build()
            );
        }

        @Test
        @DisplayName("성공 - 카테고리 목록 존재")
        void success_WithCategories() {
            // given
            given(categoryRepository.findAll()).willReturn(categories);

            // when
            List<CategoryResponse> responses = categoryService.getAllCategories();

            // then
            assertThat(responses).hasSize(3);
            assertThat(responses.getFirst().id()).isEqualTo(1L);
            assertThat(responses.getFirst().name()).isEqualTo("카테고리1");
            verify(categoryRepository, times(1)).findAll();
        }

        @Test
        @DisplayName("성공 - 카테고리 목록 비어있음")
        void success_WithEmptyCategories() {
            // given
            given(categoryRepository.findAll()).willReturn(Collections.emptyList());

            // when
            List<CategoryResponse> responses = categoryService.getAllCategories();

            // then
            assertThat(responses).isEmpty();
            verify(categoryRepository, times(1)).findAll();
        }
    }

    @Nested
    @DisplayName("카테고리 내 상품 조회")
    class GetProductInCategory {
        private Long categoryId;
        private Category category;
        private List<Product> products;

        @BeforeEach
        void setUp() {
            categoryId = 1L;
            products = Arrays.asList(
                    Product.builder().productId(1L).name("상품1").price(BigDecimal.valueOf(1000)).build(),
                    Product.builder().productId(2L).name("상품2").price(BigDecimal.valueOf(2000)).build()
            );
            category = Category.builder()
                    .id(categoryId)
                    .name("테스트 카테고리")
                    .build();

            category.addProduct(products.get(0));
            category.addProduct(products.get(1));
        }

        @Test
        @DisplayName("성공 - 상품 존재")
        void success_WithProducts() {
            // given
            given(categoryRepository.findById(categoryId)).willReturn(Optional.of(category));

            // when
            List<ProductListInCategoryResponse> responses = categoryService.getProductInCategory(categoryId);

            // then
            assertThat(responses).hasSize(2);
            assertThat(responses.get(0).categoryId()).isEqualTo(1L);
            assertThat(responses.get(0).name()).isEqualTo("상품1");
            assertThat(responses.get(1).categoryId()).isEqualTo(1L);
            assertThat(responses.get(1).name()).isEqualTo("상품2");
            verify(categoryRepository, times(1)).findById(categoryId);
        }

        @Test
        @DisplayName("성공 - 상품 없음")
        void success_WithNoProducts() {
            // given
            Category emptyCategory = Category.builder()
                    .id(categoryId)
                    .name("테스트 카테고리")
                    .build();
            given(categoryRepository.findById(categoryId)).willReturn(Optional.of(emptyCategory));

            // when
            List<ProductListInCategoryResponse> responses = categoryService.getProductInCategory(categoryId);

            // then
            assertThat(responses).isEmpty();
            verify(categoryRepository, times(1)).findById(categoryId);
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 카테고리")
        void fail_CategoryNotFound() {
            // given
            given(categoryRepository.findById(categoryId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> categoryService.getProductInCategory(categoryId))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("businessError", BusinessError.CATEGORY_NOT_FOUND);
            verify(categoryRepository, times(1)).findById(categoryId);
        }
    }

    @Nested
    @DisplayName("카테고리 조회")
    class GetCategory {
        private Long categoryId;
        private Category category;

        @BeforeEach
        void setUp() {
            categoryId = 1L;
            category = Category.builder()
                    .id(categoryId)
                    .name("테스트 카테고리")
                    .build();
        }

        @Test
        @DisplayName("성공")
        void success() {
            // given
            given(categoryRepository.findById(categoryId)).willReturn(Optional.of(category));

            // when
            Category foundCategory = categoryService.getCategory(categoryId);

            // then
            assertThat(foundCategory).isNotNull();
            assertThat(foundCategory.getId()).isEqualTo(categoryId);
            assertThat(foundCategory.getName()).isEqualTo("테스트 카테고리");
            verify(categoryRepository, times(1)).findById(categoryId);
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 카테고리")
        void fail_CategoryNotFound() {
            // given
            given(categoryRepository.findById(categoryId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> categoryService.getCategory(categoryId))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("businessError", BusinessError.CATEGORY_NOT_FOUND);
            verify(categoryRepository, times(1)).findById(categoryId);
        }
    }
}