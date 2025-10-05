package poomasi.domain.wishlist.dto.request;

import poomasi.domain.member.entity.Member;
import poomasi.domain.wishlist.entity.WishList;
import poomasi.global.common.ServiceType;

public record WishListAddRequest(
        Long objectId,
        ServiceType type
) {
    public WishList toEntity(Member member) {
        return WishList.builder()
                .member(member)
                .objectId(objectId)
                .type(type)
                .build();
    }
}
