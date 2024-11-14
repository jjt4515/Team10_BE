package poomasi.domain.wishlist.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import poomasi.domain.auth.security.userdetail.UserDetailsImpl;
import poomasi.domain.wishlist.dto.WishListDeleteRequest;
import poomasi.domain.wishlist.dto.request.WishListAddRequest;
import poomasi.domain.wishlist.service.WishListPlatformService;
import poomasi.global.common.ServiceType;

@RequestMapping("/api/v1/wishlist")
@RestController
@RequiredArgsConstructor
public class WishListPlatformController {
    private final WishListPlatformService wishListPlatformService;

    @PostMapping("/add")
    @Secured("ROLE_CUSTOMER")
    public ResponseEntity<?> addWishList(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody WishListAddRequest request) {
        wishListPlatformService.addWishList(userDetails.getMember(), request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete")
    @Secured("ROLE_CUSTOMER")
    public ResponseEntity<?> deleteWishList(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody WishListDeleteRequest request) {
        wishListPlatformService.deleteWishList(userDetails.getMember(), request);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    @Secured("ROLE_CUSTOMER")
    public ResponseEntity<?> findWishListByMemberId(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestParam String type) {
        return ResponseEntity.ok(wishListPlatformService.findWishListByMemberIdAndServiceType(userDetails.getMember().getId(), ServiceType.of(type)));
    }
}
