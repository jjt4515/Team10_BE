package poomasi.domain.store.dto;

import org.hibernate.annotations.Comment;
import poomasi.domain.member.entity.Member;
import poomasi.domain.store.entity.Store;

public record StoreRegisterRequest(
        String name,
        String address,
        String phone,
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
                .businessNumber(businessNumber)
                .shipingFee(shipingFee)
                .owner(member)
                .build();
    }
}
