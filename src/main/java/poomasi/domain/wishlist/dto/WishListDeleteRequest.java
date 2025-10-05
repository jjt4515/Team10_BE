package poomasi.domain.wishlist.dto;

import poomasi.global.common.ServiceType;

public record WishListDeleteRequest(
        Long objectId,
        ServiceType type
) {
}
