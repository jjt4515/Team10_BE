package poomasi.domain.reservation.dto.request;

import lombok.Builder;
import poomasi.domain.farm._schedule.entity.FarmSchedule;
import poomasi.domain.farm.entity.Farm;
import poomasi.domain.member.entity.Member;
import poomasi.domain.reservation.entity.Reservation;
import poomasi.domain.reservation.entity.ReservationStatus;

@Builder
public record ReservationRequest(
        Long farmId,
        Long scheduleId,

        int memberCount,
        String request
) {
    public Reservation toEntity(Member member, Farm farm, FarmSchedule farmSchedule) {
        return Reservation.builder()
                .member(member)
                .farm(farm)
                .scheduleId(farmSchedule)
                .reservationDate(farmSchedule.getDate())
                .memberCount(memberCount)
                .request(request)
                .price(farm.getExperiencePrice() * memberCount)
                .status(ReservationStatus.ACCEPTED)
                .build();
    }
}
