package poomasi.domain.aftersales.service;


import jdk.jfr.Description;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import poomasi.domain.aftersales.dto.cancel.request.FarmCancelRequest;
import poomasi.domain.aftersales.dto.cancel.response.FarmCancelResponse;
import poomasi.domain.aftersales.entity.FarmAfterSales;
import poomasi.domain.aftersales.repository.FarmAfterSalesRepository;
import poomasi.domain.auth.security.userdetail.UserDetailsImpl;
import poomasi.domain.member.entity.Member;
import poomasi.domain.reservation.entity.Reservation;
import poomasi.domain.reservation.service.ReservationService;
import poomasi.global.error.ApplicationException;
import poomasi.global.error.BusinessError;
import poomasi.global.error.BusinessException;
import poomasi.payment.entity.Payment;
import poomasi.payment.util.PaymentUtil;

import java.math.BigDecimal;

import static poomasi.domain.reservation.entity.ReservationStatus.CANCELED;
import static poomasi.domain.reservation.entity.ReservationStatus.PENDING;
import static poomasi.global.error.ApplicationError.PAYMENT_CHECKSUM_EXCESSIVE_REFUND_AMOUNT;

@Service
@RequiredArgsConstructor
public class FarmAfterSalesService implements CancelService<FarmCancelResponse, FarmCancelRequest>{

    private final FarmAfterSalesRepository farmAfterSalesRepository;
    private final ReservationService reservationService;
    private final PaymentUtil paymentUtil;


    @Override
    public FarmCancelResponse cancel(FarmCancelRequest farmCancelRequest){
        Member member = getMember();
        //1. 조회
        Long reservationId = farmCancelRequest.reservationId();
        Reservation reservation = reservationService.getReservation(reservationId);
        BigDecimal cancelAmount = reservationService.calculateRefundAmount(reservation);
        
        
        //1.1 펜딩 상태가 아니라면
        if(reservation.getStatus()!=PENDING){
            throw new BusinessException(BusinessError.RESERVATION_CANCELLATION_PERIOD_EXPIRED);
        }

        //2. farmaftersales 만들기
        FarmAfterSales farmAfterSales = FarmAfterSales
                .builder()
                .afterSalesAmount(cancelAmount)
                .reservation(reservation)
                .build();
        //checksum 검사 및 차감
        String merchantUid = reservation.getMerchantUid();
        Payment payment = reservation.getPayment();
        if(payment.isCheckSumValid(cancelAmount)){
            throw new ApplicationException(PAYMENT_CHECKSUM_EXCESSIVE_REFUND_AMOUNT);
        }
        //환불
        paymentUtil.refundByMerchantUid(merchantUid, payment.getCheckSum(), cancelAmount);
        //절차 등록
        reservation.setFarmAfterSales(farmAfterSales);
        reservation.cancel();
        farmAfterSalesRepository.save(farmAfterSales);

        return new FarmCancelResponse(reservationId, CANCELED, cancelAmount);
    }


    @Description("security context에서 member 객체 가져오는 메서드")
    private Member getMember() {
        Authentication authentication = SecurityContextHolder
                .getContext().getAuthentication();
        Object impl = authentication.getPrincipal();
        Member member = ((UserDetailsImpl) impl).getMember();
        return member;
    }

}
