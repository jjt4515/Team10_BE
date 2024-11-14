package poomasi.domain.wishlist.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poomasi.domain.member.entity.Member;
import poomasi.domain.wishlist.dto.WishListDeleteRequest;
import poomasi.domain.wishlist.dto.WishListResponse;
import poomasi.domain.wishlist.dto.request.WishListAddRequest;
import poomasi.domain.wishlist.entity.WishList;

import java.util.List;

@RequiredArgsConstructor
@Service
public class WishListPlatformService {
    private final WishListService wishListService;

    @Transactional
    public void addWishList(Member member, WishListAddRequest request) {
        wishListService.addWishList(request);
    }

    @Transactional
    public void deleteWishList(Member member, WishListDeleteRequest request) {
        wishListService.deleteWishList(request);
    }

    @Transactional(readOnly = true)
    public List<WishListResponse> findWishListByMemberId(Long memberId) {
        return wishListService.findWishListByMemberId(memberId).stream()
                .map(WishListResponse::fromEntity)
                .toList();
    }
}
