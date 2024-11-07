package poomasi.domain.product._store.dto;

import org.hibernate.annotations.Comment;
import poomasi.domain.member.entity.Member;
import poomasi.domain.product._store.entity.Store;

public record StoreRegisterRequest(
        String name,
        String address,
        String phone,
        String ownerPhone,
        @Comment("사업자 번호")
        String businessNumber,
        @Comment("배송비")
        Integer shipingFee
) {

    public Store toEntity(Member member) {
        return Store.builder()
                .name(name)
                .address(address)
                .phone(phone)
                .ownerPhone(ownerPhone)
                .businessNumber(businessNumber)
                .shipingFee(shipingFee)
                .owner(member)
                .build();
    }
}
