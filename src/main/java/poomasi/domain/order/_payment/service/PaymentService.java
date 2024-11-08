package poomasi.domain.order._payment.service;

import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.CancelData;
import com.siot.IamportRestClient.request.PrepareData;
import com.siot.IamportRestClient.response.AccessToken;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Prepare;
import jdk.jfr.Description;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poomasi.domain.auth.security.userdetail.UserDetailsImpl;
import poomasi.domain.member.entity.Member;
import poomasi.domain.order._payment.dto.request.PaymentPreRegisterRequest;
import poomasi.domain.order._payment.dto.request.PaymentWebHookRequest;
import poomasi.domain.order._payment.dto.response.PaymentPreRegisterResponse;
import poomasi.domain.order._payment.dto.response.PaymentResponse;
import poomasi.domain.order._payment.entity.Payment;
import poomasi.domain.order._payment.repository.PaymentRepository;
import poomasi.domain.order.entity.Order;
import poomasi.domain.order.repository.OrderRepository;
import poomasi.domain.product._cart.service.CartService;
import poomasi.global.error.BusinessError;
import poomasi.global.error.BusinessException;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import static poomasi.domain.order.entity.OrderStatus.AWAITING_SELLER_CONFIRMATION;
import static poomasi.domain.order.entity.OrderStatus.PENDING;
import static poomasi.global.error.BusinessError.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    @Autowired
    private final PaymentRepository paymentRepository;
    private final IamportClient iamportClient;
    private final OrderRepository orderRepository;
    private final CartService cartService;

    @Description("포트원 api 호출을 위한 accessToken 발급 메서드")
    private String getPortOneAccessToken() throws IOException, IamportResponseException {
        IamportResponse<AccessToken> authResponse = iamportClient.getAuth();
        String accessToken = authResponse.getResponse().getToken();
        return accessToken;
    }

    @Description("사전 결제 등록")
    public PaymentPreRegisterResponse portonePrePaymentRegister(PaymentPreRegisterRequest paymentPreRegisterRequest) throws IOException, IamportResponseException {
        PrepareData prepareData = new PrepareData(paymentPreRegisterRequest.merchantUid(),
                paymentPreRegisterRequest.amount()
        );
        iamportClient.postPrepare(prepareData);
        return PaymentPreRegisterResponse.from(
                paymentPreRegisterRequest.merchantUid()
        );
    }

    @Transactional
    @Description("프론트에서 받아온 결과를 validate하는 메서드")
    public void portoneVerifyPostPayment(PaymentWebHookRequest paymentWebHookRequest) throws IOException, IamportResponseException {
        String impUid = paymentWebHookRequest.imp_uid();
        String merchantUid = paymentWebHookRequest.merchant_uid();
        IamportResponse<com.siot.IamportRestClient.response.Payment> iamportResponse = iamportClient.paymentByImpUid(impUid);
        BigDecimal amount = iamportResponse.getResponse()
                .getAmount();

        Order order = orderRepository.findByMerchantUid(merchantUid)
                .orElseThrow(() -> new BusinessException(ORDER_NOT_FOUND));
        
        if(order.getOrderStatus()!=PENDING){ //이미 처리한 주문이라면
            throw new BusinessException(ORDER_ALREADY_PROCESSED);
        }

        if(validatePaymentConsistency(order.getTotalAmount(), amount)){ //결제 금액이 맞지 않다면 -> 주문 취소 api 호출
            cancelPayment(iamportResponse);
            throw new BusinessException(PAYMENT_AMOUNT_MISMATCH);
        }
        order.setOrderStatus(AWAITING_SELLER_CONFIRMATION); // 상태 변경
        cartService.removeSelected(); //장바구니 삭제
    }

    private boolean validatePaymentConsistency(BigDecimal prepaymentAmount, BigDecimal postPaymentAmount){
        if (prepaymentAmount.compareTo(postPaymentAmount) != 0) {
            return false;
        }
        return true;
    }

    @Description("payment 상세 내역 조회를 위한 단건 api 호출")
    public void getPaymentDetails(String merchantUid, Long orderId) throws IOException, IamportResponseException {

    }

    @Description("결제 취소 api")
    public void cancelPayment(IamportResponse<com.siot.IamportRestClient.response.Payment> response) throws IOException, IamportResponseException{
        //true면 Uid, false면 merchantUid로 판단
        CancelData cancelData = new CancelData(response.getResponse().getMerchantUid(), false);
        iamportClient.cancelPaymentByImpUid(cancelData);
    }

    @Description("결제 환불 api")
    public void processRefund() throws IOException, IamportResponseException{

    }


    @Description("결제 부분 환불 api 호출")
    public void partialRefund() throws IOException, IamportResponseException {

    }

    public PaymentResponse getPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new BusinessException(PAYMENT_NOT_FOUND));
        return PaymentResponse.fromEntity(payment);
    }
    
    @Description("orderID로 결제 방법 찾는 메서드")
    public PaymentResponse getPaymentByOrderId(Long orderId) {
        Member member = getMember();
        Payment payment = paymentRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(PAYMENT_NOT_FOUND));
        return PaymentResponse.fromEntity(payment);
    }


    private Member getMember() {
        Authentication authentication = SecurityContextHolder
                .getContext().getAuthentication();
        Object impl = authentication.getPrincipal();
        Member member = ((UserDetailsImpl) impl).getMember();
        return member;
    }

}


