package poomasi.domain.reservation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poomasi.domain.farm._schedule.entity.FarmSchedule;
import poomasi.domain.farm._schedule.service.FarmScheduleService;
import poomasi.domain.farm.entity.Farm;
import poomasi.domain.farm.service.FarmService;
import poomasi.domain.member.entity.Member;
import poomasi.domain.member.service.MemberService;
import poomasi.domain.reservation.dto.request.ReservationRequest;
import poomasi.domain.reservation.dto.response.ReservationResponse;
import poomasi.domain.reservation.entity.Reservation;
import poomasi.global.error.BusinessError;
import poomasi.global.error.BusinessException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationPlatformService {
    private final ReservationService reservationService;
    private final MemberService memberService;
    private final FarmService farmService;
    private final FarmScheduleService farmScheduleService;

    private final int RESERVATION_CANCELLATION_PERIOD = 3;

    @Transactional
    public ReservationResponse createReservation(ReservationRequest request) {
        Member member = memberService.findMemberById(request.memberId());
        Farm farm = farmService.getValidFarmByFarmId(request.farmId());
        FarmSchedule farmSchedule = farmScheduleService.getFarmScheduleByScheduleId(request.scheduleId());

        // 1. 농장에 최대 수용 가능 팀 확인
        int reservationCount = reservationService.getValidReservationsByFarmIdAndScheduleId(farm.getId(), farmSchedule).size();
        if (reservationCount >= farm.getMaxReservation()) {
            throw new BusinessException(BusinessError.RESERVATION_FULL);
        }


        // 2. 농장에서 최대 수용 가능 인원 확인
        if (request.memberCount() > farm.getMaxCapacity()) {
            throw new BusinessException(BusinessError.RESERVATION_MEMBER_EXCEED);
        }
        Reservation reservation = reservationService.createReservation(request.toEntity(member, farm, farmSchedule));

        return reservation.toResponse();
    }

    public ReservationResponse getReservation(Long memberId, Long reservationId) {
        Reservation reservation = reservationService.getReservationById(reservationId);
        if (!reservation.getMember().getId().equals(memberId) && !memberService.isAdmin(memberId) && !reservation.getFarm().getOwnerId().equals(memberId)) {
            throw new BusinessException(BusinessError.RESERVATION_NOT_ACCESSIBLE);
        }

        return reservation.toResponse();
    }

    @Transactional
    public void cancelReservation(Long memberId, Long reservationId) {
        Reservation reservation = reservationService.getReservationById(reservationId);

        if (!reservation.getMember().getId().equals(memberId) || !memberService.isAdmin(memberId)) {
            throw new BusinessException(BusinessError.RESERVATION_NOT_ACCESSIBLE);
        }

        if (reservation.isCanceled()) {
            throw new BusinessException(BusinessError.RESERVATION_ALREADY_CANCELED);
        }

        // 우리 아직 예약 취소 규정 정해놓지 않았으니까 일단은 3일 전에만 취소 가능하다고 가정
        if (reservation.getReservationDate().isBefore(reservation.getReservationDate().minusDays(RESERVATION_CANCELLATION_PERIOD))) {
            throw new BusinessException(BusinessError.RESERVATION_CANCELLATION_PERIOD_EXPIRED);
        }

        reservationService.cancelReservation(reservation);
    }
}
