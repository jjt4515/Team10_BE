package poomasi.domain.product._store.dto;

import java.util.List;
import org.hibernate.annotations.Comment;
import poomasi.domain.product._store.entity.Store;
import poomasi.domain.product.dto.ProductResponse;

public record StoreResponse(
        String name,
        String address,
        String phone,
        String ownerPhone,
        @Comment("사업자 번호")
        String businessNumber,
        @Comment("배송비")
        Integer shipingFee,
        String ownerName,
        List<ProductResponse> products
) {

    public static StoreResponse fromEntity(Store store) {
        return new StoreResponse(
                store.getName(),
                store.getAddress(),
                store.getPhone(),
                store.getOwnerPhone(),
                store.getBusinessNumber(),
                store.getShipingFee(),
                store.getOwner().getMemberProfile() == null ? ""
                        : store.getOwner().getMemberProfile().getName(),
                store.getProducts().stream().map(ProductResponse::fromEntity).toList()
        );
    }
}
