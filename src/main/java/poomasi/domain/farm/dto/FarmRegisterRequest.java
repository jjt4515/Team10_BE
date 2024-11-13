package poomasi.domain.farm.dto;

import poomasi.domain.farm.entity.Farm;
import poomasi.domain.farm.entity.FarmItemType;

public record FarmRegisterRequest(
        String name,
        String address,
        String addressDetail,
        Double latitude,
        Double longitude,
        String phoneNumber,
        String description,
        int experiencePrice,
        Integer maxCapacity,
        Integer maxReservation,
        FarmItemType itemType
) {
    public Farm toEntity(Long memberId) {
        return Farm.builder()
                .name(name)
                .ownerId(memberId)
                .address(address)
                .addressDetail(addressDetail)
                .latitude(latitude)
                .longitude(longitude)
                .description(description)
                .experiencePrice(experiencePrice)
                .maxCapacity(maxCapacity)
                .maxReservation(maxReservation)
                .itemType(itemType)
                .phoneNumber(phoneNumber)
                .itemType(itemType)
                .build();
    }
}
