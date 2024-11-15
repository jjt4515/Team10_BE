package poomasi.domain.reservation.dto.response;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Getter;
import poomasi.domain.reservation.entity.ReservationStatus;

import java.time.LocalDate;

@Builder
public record ReservationResponse(
        Long id,
        Long farmId,
        Long memberId,
        Long scheduleId,
        LocalDate reservationDate,
        int memberCount,
        ReservationStatus status,
        String request,
        int price,
        String merchantUid,
        boolean isReviewed
) {

}
