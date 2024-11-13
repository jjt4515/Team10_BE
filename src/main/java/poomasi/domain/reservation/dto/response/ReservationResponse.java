package poomasi.domain.reservation.dto.response;

import java.time.LocalDate;
import lombok.Builder;
import poomasi.domain.reservation.entity.ReservationStatus;

@Builder
public record ReservationResponse(Long farmId, Long memberId, Long scheduleId,
                                  LocalDate reservationDate,
                                  int memberCount, ReservationStatus status, String request,
                                  int price, boolean isReviewed

) {

}
