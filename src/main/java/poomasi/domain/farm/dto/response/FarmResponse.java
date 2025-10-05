package poomasi.domain.farm.dto.response;

import lombok.Builder;
import poomasi.domain.farm.entity.Farm;

@Builder
public record FarmResponse(
        Long id,
        String name,
        String address,
        String addressDetail,
        Double latitude,
        Double longitude,
        String description,
        int experiencePrice,
        double averageRating
) {

    public static FarmResponse fromEntity(Farm farm) {
        return FarmResponse
                .builder()
                .name(farm.getName())
                .address(farm.getAddress())
                .addressDetail(farm.getAddressDetail())
                .latitude(farm.getLatitude())
                .longitude(farm.getLongitude())
                .description(farm.getDescription())
                .experiencePrice(farm.getExperiencePrice().intValue())
                .averageRating(farm.getAverageRating())
                .build();
    }
}
