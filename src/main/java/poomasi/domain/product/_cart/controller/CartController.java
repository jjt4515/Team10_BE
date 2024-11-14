package poomasi.domain.product._cart.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import poomasi.domain.auth.security.userdetail.UserDetailsImpl;
import poomasi.domain.member.entity.Member;
import poomasi.domain.product._cart.dto.CartResponse;
import poomasi.domain.product._cart.service.CartService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    //장바구니 모든 정보
    @GetMapping("")
    public ResponseEntity<?> getCart(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        Member member = userDetails.getMember();
        List<CartResponse> cart = cartService.getCart(member);
        return ResponseEntity.ok().body(cart);
    }

    //장바구니 추가
    @PostMapping("/{productId}")
    public ResponseEntity<?> addCart(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long productId) {
        Member member = userDetails.getMember();
        Long cartId = cartService.addCart(member, productId);
        return new ResponseEntity<>(cartId, HttpStatus.CREATED);
    }

    //장바구니 삭제
    @DeleteMapping("/{cartId}")
    public ResponseEntity<?> removeCart(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long cartId) {
        Member member = userDetails.getMember();
        cartService.deleteCart(member, cartId);
        return ResponseEntity.ok().build();
    }

    //장바구니 전부 삭제
    @DeleteMapping("/all")
    public ResponseEntity<?> removeAllCart(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        Member member = userDetails.getMember();
        cartService.deleteAll(member);
        return ResponseEntity.ok().build();
    }

}
