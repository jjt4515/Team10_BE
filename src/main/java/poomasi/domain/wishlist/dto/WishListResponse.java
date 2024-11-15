package poomasi.domain.wishlist.dto;

import java.math.BigDecimal;
import java.util.List;

import poomasi.domain.image.entity.Image;
import poomasi.domain.wishlist.entity.WishList;
import poomasi.global.common.ServiceType;

public record WishListResponse(
        Long objectId,
        ServiceType type,
        BigDecimal price,
        String imageUrl,
        String description
) {
    public static WishListResponse fromEntity(WishList wishList, BigDecimal price, String imageUrl, String description) {
        return new WishListResponse(
                wishList.getObjectId(),
                wishList.getType(),
                price,
                imageUrl,
                description
        );
    }
}
