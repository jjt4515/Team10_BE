package poomasi.domain.farm.dto.response;

import poomasi.domain.farm.entity.Farm;


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
        return new FarmResponse(
                farm.getId(),
                farm.getName(),
                farm.getAddress(),
                farm.getAddressDetail(),
                farm.getLatitude(),
                farm.getLongitude(),
                farm.getDescription(),
                farm.getExperiencePrice(),
                farm.getAverageRating()
        );
    }
}
