package poomasi.domain.reservation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import poomasi.domain.farm._schedule.entity.FarmSchedule;
import poomasi.domain.farm.entity.Farm;
import poomasi.domain.reservation.dto.response.ReservationResponse;
import poomasi.domain.reservation.entity.Reservation;
import poomasi.domain.reservation.repository.ReservationRepository;
import poomasi.global.error.BusinessError;
import poomasi.global.error.BusinessException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;

    public Reservation getReservation(Long id) {
        return reservationRepository.findById(id).orElseThrow(() -> new BusinessException(BusinessError.RESERVATION_NOT_FOUND));
    }

    public List<Reservation> getReservationsByMemberId(Long memberId) {
        return reservationRepository.findAllByMemberId(memberId);
    }

    public List<Reservation> getReservationsByFarmId(Long farmId) {
        return reservationRepository.findAllByFarmId(farmId);
    }

    public Reservation createReservation(Reservation reservation) {
        return reservationRepository.save(reservation);
    }

    public Reservation getReservationById(Long reservationId) {
        return reservationRepository.findById(reservationId).orElseThrow(() -> new BusinessException(BusinessError.RESERVATION_NOT_FOUND));
    }

    public void cancelReservation(Reservation reservation) {
        reservation.cancel();
        reservationRepository.save(reservation);
    }

    public List<ReservationResponse> getReservationsByFarmerId(Long farmerId) {
        return reservationRepository.findAllByFarmId(farmerId).stream()
                .map(Reservation::toResponse)
                .toList();
    }

    public List<Reservation> getValidReservationsByFarmIdAndScheduleId(Long farmId, FarmSchedule farmSchedule) {
        return reservationRepository.findAllByFarmIdAndScheduleId(farmId, farmSchedule).stream()
                .filter(Reservation::isNotCancelled)
                .toList();
    }

    public List<Reservation> getReservationByFarmIds(List<Farm> farms) {
        return reservationRepository.findAllByFarmIn(farms);
    }

    public Reservation findByMerchantUid(String merchantUid) {
        return reservationRepository.findByMerchantUid(merchantUid).orElseThrow(() -> new BusinessException(BusinessError.RESERVATION_NOT_FOUND));
    }

    public Reservation save(Reservation reservation) {
        return reservationRepository.save(reservation);
    }
}
