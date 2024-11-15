package poomasi.domain.member.dto.request;

import poomasi.domain.store.dto.StoreRegisterRequest;

public record ConvertToFarmerRequest(
        String name,
        String address,
        String phone
) {
    public StoreRegisterRequest toStoreRegisterRequest() {
        return new StoreRegisterRequest(
                name,
                address,
                phone
        );
    }
}