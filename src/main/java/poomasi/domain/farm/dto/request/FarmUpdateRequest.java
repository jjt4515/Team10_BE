package poomasi.domain.farm.dto.request;

import poomasi.domain.farm.entity.Farm;

public record FarmUpdateRequest(
        String name,
        String description,
        String address,
        String addressDetail,
        Double latitude,
        Double longitude
) {
    public Farm toEntity(Farm farm) {
        return farm.updateFarm(this);
    }
}
