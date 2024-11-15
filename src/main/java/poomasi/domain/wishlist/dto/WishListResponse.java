package poomasi.domain.wishlist.dto;

import java.math.BigDecimal;
import java.util.List;
import poomasi.domain.image.entity.Image;
import poomasi.domain.wishlist.entity.WishList;

import java.math.BigDecimal;

public record WishListResponse(
        Long productId,
        String productName,
        BigDecimal price,
        List<Image> images,
        String description
) {
    public static WishListResponse fromEntity(WishList wishList) {
        return new WishListResponse(
                wishList.getProduct().getId(),
                wishList.getProduct().getName(),
                wishList.getProduct().getPrice(),
                wishList.getProduct().getImages(),
                wishList.getProduct().getDescription()
        );
    }
}
