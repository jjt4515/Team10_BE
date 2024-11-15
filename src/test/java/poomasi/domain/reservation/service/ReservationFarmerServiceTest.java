package poomasi.domain.reservation.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import poomasi.domain.farm._schedule.entity.FarmSchedule;
import poomasi.domain.farm.entity.Farm;
import poomasi.domain.farm.service.FarmService;
import poomasi.domain.member.entity.Member;
import poomasi.domain.reservation.dto.response.ReservationResponse;
import poomasi.domain.reservation.entity.Reservation;
import poomasi.global.error.BusinessException;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static poomasi.global.error.BusinessError.RESERVATION_NOT_ACCESSIBLE;

@ExtendWith(MockitoExtension.class)
class ReservationFarmerServiceTest {

    @Mock
    private Member member;

    @Mock
    private FarmService farmService;

    @Mock
    private ReservationService reservationService;

    @InjectMocks
    private ReservationFarmerService reservationFarmerService;

    @Nested
    @DisplayName("농장 예약 조회")
    class GetReservationsByFarmerId {

        @Test
        @DisplayName("농장 예약을 조회한다")
        void should_getReservationsByFarmerId() {
            // given
            Farm farm = Farm.builder().id(1L).ownerId(1L).experiencePrice(1000).build(); // Farm 생성
            FarmSchedule farmSchedule = FarmSchedule.builder().id(1L).build(); // FarmSchedule 생성
            List<Farm> farms = List.of(farm);

            Reservation reservation = Reservation.builder()
                    .id(1L)
                    .farm(farm)
                    .scheduleId(farmSchedule) // FarmSchedule 설정
                    .member(member)
                    .price(new BigDecimal(1000))
                    .build();

            List<Reservation> reservations = List.of(reservation);

            given(member.isFarmer()).willReturn(true);
            given(farmService.getFarmListByOwnerId(anyLong())).willReturn(farms);
            given(reservationService.getReservationByFarmIds(farms)).willReturn(reservations); // 추가된 스터빙

            // when
            List<ReservationResponse> result = reservationFarmerService.getReservationsByFarmerId(member);

            // then
            assertEquals(1, result.size());
        }

        @Test
        @DisplayName("농장 예약 조회 권한이 없는 경우")
        void should_throwException_when_notAccessible() {
            // given
            given(member.isFarmer()).willReturn(false);
            given(member.isAdmin()).willReturn(false);

            // when
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> reservationFarmerService.getReservationsByFarmerId(member));

            // then
            assertEquals(RESERVATION_NOT_ACCESSIBLE, exception.getBusinessError());
        }
    }
}
