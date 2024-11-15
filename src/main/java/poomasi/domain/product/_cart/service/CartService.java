package poomasi.domain.product._cart.service;

import java.util.List;
import java.util.Optional;
import jdk.jfr.Description;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poomasi.domain.auth.security.userdetail.UserDetailsImpl;
import poomasi.domain.member.entity.Member;
import poomasi.domain.product._cart.dto.CartResponse;
import poomasi.domain.product._cart.entity.Cart;
import poomasi.domain.product._cart.repository.CartRepository;
import poomasi.domain.product.entity.Product;
import poomasi.domain.product.service.ProductService;
import poomasi.global.error.BusinessError;
import poomasi.global.error.BusinessException;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final ProductService productService;

    public List<CartResponse> getCart(Member member) {
        return cartRepository.findByMember(member);
    }

    @Transactional
    public Long addCart(Member member, Long productId) {
        Product product = getProductById(productId);

        Optional<Cart> cartOptional =
                cartRepository.findByMemberIdAndProductId(member.getId(), product.getId());

        //이미 담은 상품임
        if (cartOptional.isPresent())
            return cartOptional.get().getId();


        Cart cart = Cart.builder()
                .member(member)
                .product(product)
                .build();

        cart = cartRepository.save(cart);
        return cart.getId();
    }

    @Transactional
    @Description("카트 한 개 삭제")
    public void deleteCart(Member member, Long cartId) {
        Cart cart = getCartById(cartId);
        checkAuth(member, cart);
        cartRepository.delete(cart);
    }


    @Transactional
    @Description("전부 삭제")
    public void deleteAll(Member member) {
        cartRepository.deleteAllByMemberId(member.getId());
    }

    @Description("요청한 사람이랑 카트 주인이랑 같은지 확인")
    private void checkAuth(Member member, Cart cart) {
        if (!member.getId().equals(cart.getMember().getId())) {
            throw new BusinessException(BusinessError.MEMBER_ID_MISMATCH);
        }
    }

    public Cart getCartById(Long cartId) {
        return cartRepository.findById(cartId)
                .orElseThrow(() -> new BusinessException(BusinessError.CART_NOT_FOUND));
    }

    private Product getProductById(Long productId) {
        return productService.findProductById(productId);
    }

    @Description("order 만들 때 사용할 거")
    public List<Cart> getCartsByIdList(List<Long> ids) {
        List<Cart> orderList = cartRepository.getCartsByIdList(ids);
        cartRepository.deleteAll(orderList);
        return orderList;
    }
}
