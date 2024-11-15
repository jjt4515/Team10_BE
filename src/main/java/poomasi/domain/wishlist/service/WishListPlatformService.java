package poomasi.domain.wishlist.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poomasi.domain.farm.service.FarmService;
import poomasi.domain.member.entity.Member;
import poomasi.domain.product.service.ProductService;
import poomasi.domain.wishlist.dto.WishListDeleteRequest;
import poomasi.domain.wishlist.dto.WishListResponse;
import poomasi.domain.wishlist.dto.request.WishListAddRequest;
import poomasi.global.common.ServiceType;
import poomasi.global.error.ApplicationError;
import poomasi.global.error.ApplicationException;

import java.util.List;

@RequiredArgsConstructor
@Service
public class WishListPlatformService {
    private final WishListService wishListService;
    private final ProductService productService;
    private final FarmService farmService;

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
        if (type.equals(ServiceType.FARM)) {
            return wishListService.findWishListByMemberId(memberId).stream()
                    .map(wishList -> WishListResponse.fromEntity(wishList, productService.findProductById(wishList.getObjectId()).getPrice(), productService.findProductById(wishList.getObjectId()).getImages().getFirst().getImageUrl(), productService.findProductById(wishList.getObjectId()).getDescription()))
                    .toList();

        } else if (type.equals(ServiceType.PRODUCT)) {
            return wishListService.findWishListByMemberId(memberId).stream()
                    .map(wishList -> WishListResponse.fromEntity(wishList, farmService.getFarmByFarmId(wishList.getObjectId()).getExperiencePrice(), farmService.getFarmByFarmId(wishList.getObjectId()).getMainImage(), farmService.getFarmByFarmId(wishList.getObjectId()).getDescription()))
                    .toList();
        }

        throw new ApplicationException(ApplicationError.ENUM_TYPE_ERROR);
    }
}
