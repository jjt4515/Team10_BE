package poomasi.domain.wishlist.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poomasi.domain.member.entity.Member;
import poomasi.domain.wishlist.dto.WishListDeleteRequest;
import poomasi.domain.wishlist.dto.WishListResponse;
import poomasi.domain.wishlist.dto.request.WishListAddRequest;
import poomasi.global.common.ServiceType;

import java.util.List;

@RequiredArgsConstructor
@Service
public class WishListPlatformService {
    private final WishListService wishListService;

    @Transactional
    public void addWishList(Member member, WishListAddRequest request) {
        wishListService.addWishList(member, request);
    }

    @Transactional
    public void deleteWishList(Member member, WishListDeleteRequest request) {
        wishListService.deleteWishList(member, request);
    }

    @Transactional(readOnly = true)
    public List<WishListResponse> findWishListByMemberIdAndServiceType(Long memberId, ServiceType type) {
        return wishListService.findWishListByMemberIdAndServiceType(memberId, type).stream()
                .map(WishListResponse::fromEntity)
                .toList();
    }
}
