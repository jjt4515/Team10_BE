package poomasi.domain.store.dto;

import lombok.Builder;
import org.hibernate.annotations.Comment;
import org.jetbrains.annotations.NotNull;
import poomasi.domain.product.dto.ProductResponse;
import poomasi.domain.store.entity.Store;

import java.util.List;

@Builder
public record StoreResponse(
        Long id,

        @NotNull
        String name,

        @NotNull
        String address,

        String phone,

        @Comment("사업자 번호")
        // @NotNull
        String businessNumber,

        @NotNull
        String ownerName,

        List<ProductResponse> products
) {

    public static StoreResponse fromEntity(Store store) {
        return StoreResponse.builder()
                .id(store.getId())
                .name(store.getName())
                .address(store.getAddress())
                .phone(store.getPhone())
                .businessNumber(store.getBusinessNumber())
                //TODO 나중에 삼항연산자 삭제
                .ownerName( store.getOwner().getName() == null ? ""
                                : store.getOwner().getName())
                .products(store.getProducts().stream().map(ProductResponse::fromEntity).toList())
                .build();

    }
}