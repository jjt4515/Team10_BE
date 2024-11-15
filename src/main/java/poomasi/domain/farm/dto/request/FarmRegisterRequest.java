package poomasi.domain.farm.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import poomasi.domain.farm.entity.Farm;

import java.time.LocalDate;
import java.time.LocalTime;

@Builder
public record FarmRegisterRequest(
        @NotNull
        String name,
        @NotNull
        String address,
        @NotNull
        String growEnv,
        String addressDetail,
        @NotNull
        Double latitude,
        @NotNull
        Double longitude,
        @NotNull
        String phoneNumber,

        String description,
        @NotNull
        int experiencePrice,
        @NotNull
        Integer maxPeople,
        @NotNull
        Integer maxTeam,
        @NotNull
        Long categoryId,
        @NotNull
        String imageUrl,
        @NotNull
        int price,

        @NotNull
        LocalDate startDate,

        @NotNull
        LocalDate endDate,

        @NotNull
        LocalTime startTime,

        @NotNull
        LocalTime endTime,

        @NotNull
        String businessNumber,

        FarmInfoAggregateRequest info
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
                .maxCapacity(maxPeople)
                .maxReservation(maxTeam)
                .categoryId(categoryId)
                .phoneNumber(phoneNumber)
                .mainImage(imageUrl)
                .growEnv(growEnv)
                .experiencePrice(price)
                .businessNumber(businessNumber)
                .build();
    }
}
