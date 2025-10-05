package poomasi.domain.store.dto;

import poomasi.domain.member.entity.Member;
import poomasi.domain.store.entity.Store;

public record StoreRegisterRequest(
        String name,
        String address,
        String phone
) {

    public Store toEntity(Member member) {
        return Store.builder()
                .name(name)
                .address(address)
                .phone(phone)
                .owner(member)
                .build();
    }
}
