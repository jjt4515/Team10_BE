package poomasi.domain.aftersales.dto.cancel.response;

import poomasi.domain.reservation.entity.ReservationStatus;

import java.math.BigDecimal;

public record FarmCancelResponse(Long reservationId,
                                 ReservationStatus reservationStatus,
                                 BigDecimal finalCancelAmount) {
}
