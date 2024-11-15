package poomasi.domain.wishlist.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poomasi.domain.member.entity.Member;
import poomasi.domain.product.entity.Product;
import poomasi.domain.product.service.ProductService;
import poomasi.domain.wishlist.dto.WishListDeleteRequest;
import poomasi.domain.wishlist.dto.request.WishListAddRequest;
import poomasi.domain.wishlist.entity.WishList;
import poomasi.domain.wishlist.repository.WishListRepository;
import poomasi.global.common.ServiceType;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WishListService {
    private final WishListRepository wishListRepository;
    private final ProductService productService;

    @Transactional
    public void addWishList(Member member, WishListAddRequest request) {
        Product product = productService.findProductById(request.objectId());
        wishListRepository.save(request.toEntity(member));
    }

    @Transactional
    public void deleteWishList(Member member, WishListDeleteRequest request) {
        wishListRepository.deleteByMemberIdAndObjectIdAndType(member.getId(), request.objectId(), request.type());
    }


    public List<WishList> findWishListByMemberIdAndServiceType(Long memberId, ServiceType type) {
        return wishListRepository.findByMemberId(memberId);
    }

    public List<WishList> findWishListByMemberId(Long memberId) {
        return wishListRepository.findByMemberId(memberId);
    }
}
