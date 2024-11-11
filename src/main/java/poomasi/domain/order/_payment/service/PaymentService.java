package poomasi.domain.order._payment.service;

import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.CancelData;
import com.siot.IamportRestClient.request.PrepareData;
import com.siot.IamportRestClient.response.IamportResponse;
import jdk.jfr.Description;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import poomasi.domain.auth.security.userdetail.UserDetailsImpl;
import poomasi.domain.member.entity.Member;
import poomasi.domain.order._payment.dto.request.PaymentPreRegisterRequest;
import poomasi.domain.order._payment.dto.request.PaymentWebHookRequest;
import poomasi.domain.order._payment.dto.response.PaymentPreRegisterResponse;
import poomasi.domain.order._payment.dto.response.PaymentResponse;
import poomasi.domain.order._payment.entity.Payment;
import poomasi.domain.order._payment.repository.PaymentRepository;
import poomasi.domain.order.entity._product.OrderedProduct;
import poomasi.domain.order.entity._product.ProductOrder;
import poomasi.domain.order.repository.ProductOrderRepository;
import poomasi.domain.product._cart.service.CartService;
import poomasi.domain.product.entity.Product;
import poomasi.global.error.BusinessException;
import poomasi.global.error.PaymentConfirmError;
import poomasi.global.error.PaymentConfirmException;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

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
    private final ProductOrderRepository productOrderRepository;
    private final CartService cartService;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final AtomicBoolean isWebhookReceived = new AtomicBoolean(false); // 웹훅 수신 여부 체크
    
    @Description("사전 결제 등록. 프론트엔드에게 서버 merchant uid를 return 해야 함")
    public PaymentPreRegisterResponse portonePrePaymentRegister(PaymentPreRegisterRequest paymentPreRegisterRequest) throws IOException, IamportResponseException {
        
        PrepareData prepareData = new PrepareData(paymentPreRegisterRequest.merchantUid(),
                paymentPreRegisterRequest.amount()
        );
        iamportClient.postPrepare(prepareData);
        return PaymentPreRegisterResponse.from(
                paymentPreRegisterRequest.merchantUid()
        );
    }


    @Transactional(isolation = Isolation.SERIALIZABLE)
    @Description("포트원 결제 직전 바로 받는 confirm 요청. 40초 대기")
    public void confirmProductStock(String impUid, String merchantUid) throws IOException, IamportResponseException {

        ProductOrder productOrder = productOrderRepository.findByMerchantUidAndImpUid(merchantUid, impUid)
                .orElseThrow(() -> new BusinessException(PAYMENT_NOT_FOUND));

        List<OrderedProduct> orderedProductList = productOrder.getOrderedProducts();
        //수량 검증
        for(OrderedProduct orderedProduct : orderedProductList) {
            Product product = orderedProduct.getProduct();
            Integer remainQuantity = product.getStock();
            Integer orderQuantity = orderedProduct.getCount();

            //주문 재고가 남은 재고보다 많다면 500 + reason 보내야 함
            if(orderQuantity > remainQuantity){
                throw new PaymentConfirmException(PaymentConfirmError.PAYMENT_PROUCT_CONFIRM_EXCEPTION);
            }
        }
        BigDecimal amountToBePaid = productOrder.getTotalAmount();
        //재고 검증 완료 -> 200 OK 보내야 함 + 웹훅 수신 여부에 따라 분기
        scheduler.schedule(() -> {
            try {
                if (!isWebhookReceived.get()) { //수신 못 받으면
                    if(sendAndValidateAmount(impUid, amountToBePaid)){ //impUid를 가지고 포트원 서버에 요청을 한 후, db와 결제 금액 비교한다.
                        productOrder.setOrderStatus(AWAITING_SELLER_CONFIRMATION);
                        decreaseStock(productOrder);
                    }else{
                        //실제 결제 된 금액과 결제 되어야 할 금액이 다르다면 -> 결제 취소 api를 호출해야 한다.
                        cancelPaymentByImpUid(impUid);
                    }
                }
            } catch (IOException | IamportResponseException e) {
                e.printStackTrace();
            }
        }, 40, TimeUnit.SECONDS);

    }

    @Description("단건 조회 후, 결제 되어야 할 금액과 결제 된 금액이 같은지 확인하는 메서드")
    public boolean sendAndValidateAmount(String impUid, BigDecimal amountToBePaid) throws IOException, IamportResponseException{
        BigDecimal amount = getPaymentAmount(impUid);
        if(amountToBePaid.compareTo(amount)==0){
            return true;
        }
        return false;
    }

    
    @Description("포트원에서 결제 금액 조회하는 메서드")
    public BigDecimal getPaymentAmount(String impUid) throws IOException, IamportResponseException{
        IamportResponse<com.siot.IamportRestClient.response.Payment> iamportResponse = getSingleTransaction(impUid);
        return iamportResponse.getResponse().getAmount();
    }

    @Description("웹훅 처리 service -> 결제 정상적으로 성공됨을 보장")
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void handlePortOneProductWebhookEvent(PaymentWebHookRequest paymentWebHookRequest) throws IOException, IamportResponseException {

        isWebhookReceived.set(true); //웹훅 수신 플래그 설정하기

        String impUid = paymentWebHookRequest.impUid();
        ProductOrder productOrder = productOrderRepository.findByImpUid(impUid)
                .orElseThrow(() -> new BusinessException(PAYMENT_NOT_FOUND));
        BigDecimal amountToBePaid = productOrder.getTotalAmount();

        //결제 되어야 할 금액과 결제 된 금액이 같다면
        if(sendAndValidateAmount(impUid, amountToBePaid)){
            productOrder.setOrderStatus(AWAITING_SELLER_CONFIRMATION);
            decreaseStock(productOrder);
        }else{
            cancelPaymentByImpUid(impUid);  //실제 결제 된 금액과 결제 되어야 할 금액이 다르다면 -> 결제 취소 api를 호출해야 한다.
            //throw new BusinessException
        }

    }

    @Description("재고 차감 메서드")
    @Transactional(isolation =  Isolation.SERIALIZABLE)
    public void decreaseStock(ProductOrder productOrder){
        List<OrderedProduct> orderedProductList = productOrder.getOrderedProducts();
        for (OrderedProduct orderedProduct : orderedProductList){
            Product product = orderedProduct.getProduct();
            Integer remainQuantity = product.getStock(); //남은 수량
            Integer subtractQuantity = orderedProduct.getCount();//빼야 할 수량
            if(subtractQuantity > remainQuantity){
                throw new BusinessException(STOCK_QUANTITY_EXCEEDED);
            }
            product.subtractStock(subtractQuantity);
        }
    }

    /*
    com.siot.IamportRestClient.response.Payment payment = iamportResponse.getResponse();
    int code = iamportResponse.getCode();
    String message = iamportResponse.getMessage();
    String status = payment.getStatus();
    BigDecimal amount = payment.getAmount();
    */
    @Description("단건 결제 조회 API")
    public IamportResponse<com.siot.IamportRestClient.response.Payment> getSingleTransaction(String impUid) throws IOException, IamportResponseException {
        IamportResponse<com.siot.IamportRestClient.response.Payment> iamportResponse = iamportClient.paymentByImpUid(impUid);
        return iamportResponse;
    }

    @Description("서버에서 마지막 검증")
    public boolean verifyPostPayment(String impUid) throws IOException, IamportResponseException {
        ProductOrder productOrder = productOrderRepository.findByImpUid(impUid)
                .orElseThrow(() -> new BusinessException(PAYMENT_NOT_FOUND));
        if(productOrder.getOrderStatus() == PENDING){
            throw new BusinessException(PAYMENT_BAD_REQUEST);
        }
        return true;
    }

    /*@Transactional
    @Description("프론트에서 받아온 결과를 validate하는 메서드")
    public void portoneVerifyPostPayment(PaymentWebHookRequest paymentWebHookRequest) throws IOException, IamportResponseException {
        String impUid = paymentWebHookRequest.imp_uid();
        String merchantUid = paymentWebHookRequest.merchant_uid();
        IamportResponse<com.siot.IamportRestClient.response.Payment> iamportResponse = iamportClient.paymentByImpUid(impUid);
        BigDecimal amount = iamportResponse.getResponse()
                .getAmount();

        ProductOrder order = productOrderRepository.findByMerchantUid(merchantUid)
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
    }*/

    //private boolean compareCartAndPaymentAmount(Cart cart, BigDecimal )


    private boolean validatePaymentConsistency(BigDecimal prepaymentAmount, BigDecimal postPaymentAmount){
        if (prepaymentAmount.compareTo(postPaymentAmount) != 0) {
            return false;
        }
        return true;
    }


    public void cancelPaymentByImpUid(String impUid) throws IOException, IamportResponseException {
        CancelData cancelDate = new CancelData(impUid, false);
        iamportClient.cancelPaymentByImpUid(cancelDate);
    }

    /*@Transactional
    @Description("결제 전액 취소/환불 api")
    public void cancelPayment(Payment payment, IamportResponse<com.siot.IamportRestClient.response.Payment> response) throws IOException, IamportResponseException{
        //TODO : 결제내역 단건 조회 통해서 이미 완료가 된 결제인지 확인
        //true면 Uid, false면 merchantUid로 판단
        CancelData cancelData = new CancelData(response.getResponse().getMerchantUid(), false);
        iamportClient.cancelPaymentByImpUid(cancelData);

    }*/

   /* @Description("결제 환불 api")
    public void processRefund() throws IOException, IamportResponseException{

    }
*/

    @Transactional
    @Description("결제 부분 환불 api 호출")
    public void partialRefund(BigDecimal checkSum, IamportResponse<com.siot.IamportRestClient.response.Payment> response, BigDecimal amount) throws IOException, IamportResponseException {
        //BigDecimal checkSum = payment.getChecksum();
        CancelData cancelData = new CancelData(response.getResponse().getMerchantUid(), false, amount);
        cancelData.setChecksum(checkSum);
        iamportClient.cancelPaymentByImpUid(cancelData);

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


