package poomasi.domain.reservation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poomasi.domain.farm.entity.Farm;
import poomasi.domain.farm.service.FarmService;
import poomasi.domain.member.entity.Member;
import poomasi.domain.reservation.dto.response.ReservationResponse;
import poomasi.domain.reservation.entity.Reservation;
import poomasi.global.error.BusinessError;
import poomasi.global.error.BusinessException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationFarmerService {
    private final ReservationService reservationService;
    private final FarmService farmService;

    @Transactional(readOnly = true)
    public List<ReservationResponse> getReservationsByFarmerId(Member member) {
        if (!member.isFarmer() && !member.isAdmin()) {
            throw new BusinessException(BusinessError.RESERVATION_NOT_ACCESSIBLE);
        }

        List<Farm> farms = farmService.getFarmListByOwnerId(member.getId());

        List<Reservation> reservations = reservationService.getReservationByFarmIds(farms);
        return reservations.stream()
                .map(Reservation::toResponse)
                .toList();
    }
}
