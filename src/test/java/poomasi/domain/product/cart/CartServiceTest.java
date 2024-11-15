package poomasi.domain.product.cart;

import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import poomasi.domain.member.entity.Member;
import poomasi.domain.product._cart.dto.CartResponse;
import poomasi.domain.product._cart.entity.Cart;
import poomasi.domain.product._cart.repository.CartRepository;
import poomasi.domain.product._cart.service.CartService;
import poomasi.domain.product.entity.Product;
import poomasi.domain.product.service.ProductService;
import poomasi.global.error.BusinessError;
import poomasi.global.error.BusinessException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @InjectMocks
    private CartService cartService;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductService productService;

    private Member member;
    private Product product;
    private Cart cart;

    @BeforeEach
    void setUp() {
        member = Member.builder()
                .id(1L)
                .email("test@test.com")
                .build();

        product = Product.builder()
                .productId(1L)
                .name("테스트 상품")
                .price(BigDecimal.valueOf(10000))
                .build();

        cart = Cart.builder()
                .id(1L)
                .member(member)
                .product(product)
                .build();
    }

    @Nested
    @DisplayName("장바구니 조회")
    class GetCart {
        @Test
        @DisplayName("성공")
        void success() {
            // given
            List<CartResponse> expectedResponses = Arrays.asList(
                    new CartResponse(1L, "상품1",BigDecimal.valueOf(10000),"asd"),
                    new CartResponse(2L, "상품2",BigDecimal.valueOf(20000),"asdf")
            );
            given(cartRepository.findByMember(member)).willReturn(expectedResponses);

            // when
            List<CartResponse> responses = cartService.getCart(member);

            // then
            assertThat(responses).hasSize(2);
            verify(cartRepository).findByMember(member);
        }
    }

    @Nested
    @DisplayName("장바구니 추가")
    class AddCart {
        @Test
        @DisplayName("성공 - 새로운 상품")
        void success_NewProduct() {
            // given
            given(productService.findProductById(product.getId())).willReturn(product);
            given(cartRepository.findByMemberIdAndProductId(member.getId(), product.getId()))
                    .willReturn(Optional.empty());
            given(cartRepository.save(any(Cart.class))).willReturn(cart);

            // when
            Long cartId = cartService.addCart(member, product.getId());

            // then
            assertThat(cartId).isEqualTo(cart.getId());
            verify(cartRepository).save(any(Cart.class));
        }

        @Test
        @DisplayName("성공 - 이미 존재하는 상품")
        void success_ExistingProduct() {
            // given
            given(productService.findProductById(product.getId())).willReturn(product);
            given(cartRepository.findByMemberIdAndProductId(member.getId(), product.getId()))
                    .willReturn(Optional.of(cart));

            // when
            Long cartId = cartService.addCart(member, product.getId());

            // then
            assertThat(cartId).isEqualTo(cart.getId());
            verify(cartRepository, never()).save(any(Cart.class));
        }
    }

    @Nested
    @DisplayName("장바구니 삭제")
    class DeleteCart {
        @Test
        @DisplayName("성공")
        void success() {
            // given
            given(cartRepository.findById(cart.getId())).willReturn(Optional.of(cart));

            // when
            cartService.deleteCart(member, cart.getId());

            // then
            verify(cartRepository).delete(cart);
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 장바구니")
        void fail_CartNotFound() {
            // given
            given(cartRepository.findById(cart.getId())).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> cartService.deleteCart(member, cart.getId()))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("businessError", BusinessError.CART_NOT_FOUND);
        }

        @Test
        @DisplayName("실패 - 권한 없음")
        void fail_UnauthorizedAccess() {
            // given
            Member otherMember = Member.builder().id(2L).build();
            given(cartRepository.findById(cart.getId())).willReturn(Optional.of(cart));

            // when & then
            assertThatThrownBy(() -> cartService.deleteCart(otherMember, cart.getId()))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("businessError", BusinessError.MEMBER_ID_MISMATCH);
        }
    }

    @Nested
    @DisplayName("장바구니 전체 삭제")
    class DeleteAll {
        @Test
        @DisplayName("성공")
        void success() {
            // when
            cartService.deleteAll(member);

            // then
            verify(cartRepository).deleteAllByMemberId(member.getId());
        }
    }

    @Nested
    @DisplayName("ID 리스트로 장바구니 조회")
    class GetCartsByIdList {
        @Test
        @DisplayName("성공")
        void success() {
            // given
            List<Long> cartIds = Arrays.asList(1L, 2L);
            List<Cart> carts = Arrays.asList(
                    Cart.builder().id(1L).build(),
                    Cart.builder().id(2L).build()
            );
            given(cartRepository.getCartsByIdList(cartIds)).willReturn(carts);

            // when
            List<Cart> result = cartService.getCartsByIdList(cartIds);

            // then
            assertThat(result).hasSize(2);
            verify(cartRepository).deleteAll(carts);
        }
    }

    @Nested
    @DisplayName("장바구니 ID로 조회")
    class GetCartById {
        @Test
        @DisplayName("성공")
        void success() {
            // given
            given(cartRepository.findById(cart.getId())).willReturn(Optional.of(cart));

            // when
            Cart result = cartService.getCartById(cart.getId());

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(cart.getId());
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 장바구니")
        void fail_CartNotFound() {
            // given
            given(cartRepository.findById(cart.getId())).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> cartService.getCartById(cart.getId()))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("businessError", BusinessError.CART_NOT_FOUND);
        }
    }
}
