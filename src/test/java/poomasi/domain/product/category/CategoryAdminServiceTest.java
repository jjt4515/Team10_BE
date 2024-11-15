package poomasi.domain.product.category;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import poomasi.domain.product._category.dto.CategoryRequest;
import poomasi.domain.product._category.entity.Category;
import poomasi.domain.product._category.repository.CategoryRepository;
import poomasi.domain.product._category.service.CategoryAdminService;
import poomasi.global.error.BusinessError;
import poomasi.global.error.BusinessException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryAdminServiceTest {

    @InjectMocks
    private CategoryAdminService categoryAdminService;

    @Mock
    private CategoryRepository categoryRepository;

    @Nested
    @DisplayName("카테고리 등록")
    class RegisterCategory {
        private CategoryRequest request;
        private Category category;

        @BeforeEach
        void setUp() {
            request = new CategoryRequest("테스트 카테고리");
            category = request.toEntity();
            category = Category.builder()
                    .id(1L)
                    .name("테스트 카테고리")
                    .build();
        }

        @Test
        @DisplayName("성공")
        void success() {
            // given
            given(categoryRepository.save(any(Category.class))).willReturn(category);

            // when
            Long savedId = categoryAdminService.registerCategory(request);

            // then
            assertThat(savedId).isEqualTo(1L);
            verify(categoryRepository, times(1)).save(any(Category.class));
        }
    }

    @Nested
    @DisplayName("카테고리 수정")
    class ModifyCategory {
        private Long categoryId;
        private CategoryRequest request;
        private Category category;

        @BeforeEach
        void setUp() {
            categoryId = 1L;
            request = new CategoryRequest("수정된 카테고리");
            category = Category.builder()
                    .id(categoryId)
                    .name("기존 카테고리")
                    .build();
        }

        @Test
        @DisplayName("성공")
        void success() {
            // given
            given(categoryRepository.findById(categoryId)).willReturn(Optional.of(category));

            // when
            categoryAdminService.modifyCategory(categoryId, request);

            // then
            assertThat(category.getName()).isEqualTo(request.name());
            verify(categoryRepository, times(1)).findById(categoryId);
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 카테고리")
        void fail_CategoryNotFound() {
            // given
            given(categoryRepository.findById(categoryId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> categoryAdminService.modifyCategory(categoryId, request))
                    .isInstanceOf(BusinessException.class);
            verify(categoryRepository, times(1)).findById(categoryId);
        }
    }

    @Nested
    @DisplayName("카테고리 삭제")
    class DeleteCategory {
        private Long categoryId;
        private Category category;

        @BeforeEach
        void setUp() {
            categoryId = 1L;
            category = Category.builder()
                    .id(categoryId)
                    .name("삭제할 카테고리")
                    .build();
        }

        @Test
        @DisplayName("성공")
        void success() {
            // given
            given(categoryRepository.findById(categoryId)).willReturn(Optional.of(category));

            // when
            categoryAdminService.deleteCategory(categoryId);

            // then
            verify(categoryRepository, times(1)).findById(categoryId);
            verify(categoryRepository, times(1)).delete(category);
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 카테고리")
        void fail_CategoryNotFound() {
            // given
            given(categoryRepository.findById(categoryId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> categoryAdminService.deleteCategory(categoryId))
                    .isInstanceOf(BusinessException.class);
            verify(categoryRepository, times(1)).findById(categoryId);
            verify(categoryRepository, never()).delete(any());
        }
    }
}
