package poomasi.domain.reservation.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import poomasi.domain.farm._schedule.entity.FarmSchedule;
import poomasi.domain.farm._schedule.service.FarmScheduleService;
import poomasi.domain.farm.entity.Farm;
import poomasi.domain.farm.service.FarmService;
import poomasi.domain.member.entity.Member;
import poomasi.domain.reservation.dto.request.ReservationRequest;
import poomasi.domain.reservation.dto.response.ReservationResponse;
import poomasi.domain.reservation.entity.Reservation;
import poomasi.global.error.BusinessException;
import poomasi.payment.entity.ItemType;
import poomasi.payment.util.PaymentUtil;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ReservationPlatformServiceTest {

    @Mock
    private ReservationService reservationService;

    @Mock
    private FarmService farmService;

    @Mock
    private FarmScheduleService farmScheduleService;

    @Mock
    private PaymentUtil paymentUtil;

    @InjectMocks
    private ReservationPlatformService reservationPlatformService;

    private Member member;
    private Farm farm;
    private FarmSchedule farmSchedule;
    private ReservationRequest request;

    @BeforeEach
    void setUp() {
        member = Member.builder().id(1L).build();
        farm = Farm.builder().id(1L).ownerId(1L).maxReservation(5).maxCapacity(10).build();
        farmSchedule = FarmSchedule
                .builder()
                .id(1L)
                .startTime(LocalTime.from(LocalDate.now().atStartOfDay()))
                .endTime(LocalTime.from(LocalDate.now().atStartOfDay().plusHours(3)))
                .date(LocalDate.now())
                .farmId(1L)
                .build();

        request = ReservationRequest
                .builder()
                .farmId(1L)
                .scheduleId(1L)
                .memberCount(5)
                .build();
    }

    @Nested
    @DisplayName("예약 생성 테스트")
    class CreateReservation {

        @Test
        @DisplayName("예약 생성 성공")
        void shouldCreateReservationSuccessfully() {
            // given
            given(farmService.getValidFarmByFarmId(anyLong())).willReturn(farm);
            given(farmScheduleService.getFarmScheduleByScheduleId(anyLong())).willReturn(farmSchedule);
            given(reservationService.getValidReservationsByFarmIdAndScheduleId(anyLong(), any())).willReturn(List.of());
            given(paymentUtil.createMerchantUid(ItemType.PRODUCT)).willReturn("merchant_uid");
            Reservation reservation = Reservation.builder()
                    .id(1L)
                    .farm(farm)
                    .scheduleId(farmSchedule)
                    .member(member)
                    .price(new BigDecimal("1000"))
                    .build();
            given(reservationService.createReservation(any())).willReturn(reservation);

            // when
            ReservationResponse response = reservationPlatformService.createReservation(member, request);

            // then
            assertNotNull(response);
            assertEquals(1L, response.id());
            verify(paymentUtil).sendPrepareData("merchant_uid", reservation.getPrice());
        }

        @Test
        @DisplayName("최대 예약 수용 초과로 예약 생성 실패")
        void shouldFailWhenMaxReservationExceeded() {
            // given
            given(farmService.getValidFarmByFarmId(anyLong())).willReturn(farm);
            given(farmScheduleService.getFarmScheduleByScheduleId(anyLong())).willReturn(farmSchedule);
            given(reservationService.getValidReservationsByFarmIdAndScheduleId(anyLong(), any())).willReturn(List.of(Reservation.builder().build(), Reservation.builder().build(), Reservation.builder().build(), Reservation.builder().build(), Reservation.builder().build()));

            // when & then
            assertThrows(BusinessException.class, () -> reservationPlatformService.createReservation(member, request));
        }
    }

    @Nested
    @DisplayName("예약 조회 테스트")
    class GetReservation {

        @Test
        @DisplayName("예약 조회 성공")
        void shouldGetReservationSuccessfully() {
            // given
            Reservation reservation = Reservation.builder()
                    .id(1L)
                    .farm(farm)
                    .scheduleId(farmSchedule)
                    .member(member)
                    .price(new BigDecimal(10000))
                    .reservationDate(farmSchedule.getDate())
                    .build();
            given(reservationService.getReservationById(1L)).willReturn(reservation);

            // when
            ReservationResponse response = reservationPlatformService.getReservation(member, 1L);

            // then
            assertNotNull(response);
            assertEquals(1L, response.id());
        }

        @Test
        @DisplayName("권한이 없어 예약 조회 실패")
        void shouldFailWhenNoAccessRights() {
            // given
            Member otherMember = Member.builder().id(2L).build();
            Farm farm = Farm.builder().id(1L).ownerId(3L).build(); // farm의 ownerId는 3L
            Member member = Member.builder().id(1L).build(); // 요청하는 member의 id는 1L
            Reservation reservation = Reservation.builder()
                    .id(1L)
                    .farm(farm)
                    .scheduleId(farmSchedule)
                    .member(otherMember) // 예약자는 otherMember
                    .price(new BigDecimal(10000))
                    .reservationDate(farmSchedule.getDate())
                    .build();

            given(reservationService.getReservationById(1L)).willReturn(reservation);

            // when & then
            assertThrows(BusinessException.class, () -> reservationPlatformService.getReservation(member, 1L));
        }
    }

    @Nested
    @DisplayName("예약 취소 테스트")
    class CancelReservation {

        @Test
        @DisplayName("예약 취소 성공")
        void shouldCancelReservationSuccessfully() {
            // given
            Reservation reservation = Reservation.builder()
                    .id(1L)
                    .farm(farm)
                    .scheduleId(farmSchedule)
                    .member(member)
                    .reservationDate(farmSchedule.getDate())
                    .build();

            given(reservationService.getReservationById(1L)).willReturn(reservation);

            // when
            reservationPlatformService.cancelReservation(member, 1L);

            // then
            verify(reservationService).cancelReservation(reservation);
        }

        @Test
        @DisplayName("권한이 없어 예약 취소 실패")
        void shouldFailToCancelWhenNoAccessRights() {
            // given
            Member otherMember = Member.builder().id(2L).build();
            Reservation reservation = Reservation.builder()
                    .id(1L)
                    .farm(farm)
                    .scheduleId(farmSchedule)
                    .member(otherMember)
                    .build();
            given(reservationService.getReservationById(1L)).willReturn(reservation);

            // when & then
            assertThrows(BusinessException.class, () -> reservationPlatformService.cancelReservation(member, 1L));
        }
    }
}
