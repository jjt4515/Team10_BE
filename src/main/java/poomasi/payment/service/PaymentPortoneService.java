package poomasi.payment.service;

import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.response.IamportResponse;
//import com.siot.IamportRestClient.response.Payment;
import poomasi.domain.order.dto.response.OrderResponse;
import poomasi.domain.reservation.dto.response.ReservationResponse;
import poomasi.payment.dto.request.PaymentValidateRequest;
import poomasi.payment.entity.Payment;
import java.io.IOException;
import java.time.LocalDateTime;

import jdk.jfr.Description;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import poomasi.domain.order.entity.Order;
import poomasi.domain.order.entity.OrderedProduct;
import poomasi.domain.order.service.OrderService;
import poomasi.domain.product.entity.Product;
import poomasi.domain.product.service.ProductService;
import poomasi.domain.reservation.entity.Reservation;
import poomasi.domain.reservation.service.ReservationService;
import poomasi.global.error.ApplicationException;
import poomasi.global.error.BusinessError;
import poomasi.global.error.BusinessException;
import poomasi.global.error.PaymentConfirmError;
import poomasi.global.error.PaymentConfirmException;
import poomasi.payment.dto.request.PaymentWebHookRequest;
import poomasi.payment.entity.ItemType;
import poomasi.payment.entity.PaymentStatus;
import poomasi.payment.repository.PaymentRepository;
import poomasi.payment.util.PaymentUtil;

import java.math.BigDecimal;
import java.util.List;

import static poomasi.domain.order.entity.OrderedProductStatus.ORDERED_CANCEL;
import static poomasi.domain.order.entity.OrderedProductStatus.PAYMENT_CANCELLED;
import static poomasi.global.error.ApplicationError.PAYMENT_AMOUNT_MISMATCH;
import static poomasi.global.error.ApplicationError.PAYMENT_BAD_REQUEST;
import static poomasi.payment.entity.PaymentStatus.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentPortoneService implements PaymentService {
    private final PaymentUtil paymentUtil;
    private final ProductService productService;
    private final OrderService orderService;
    private final ReservationService reservationService;
    private final IamportClient iamportClient;
    private final PaymentRepository paymentRepository;

    @Override
    @Description("사전 결제 등록. 프론트엔드에게 서버 merchant uid를 return 해야 함")
    public void prepaymentRegister(String merchantUid, BigDecimal amount) {
        paymentUtil.sendPrepareData(merchantUid, amount);
        //return PaymentPreRegisterResponse.from(paymentPreRegisterRequest.merchantUid());
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    @Description("포트원 결제 직전 바로 받는 confirm 요청. 40초 대기: 결제 전에 재고 확인")
    public void confirmBeforePayment(String impUid, String merchantUid) {
        if (paymentUtil.checkItemType(merchantUid).equals(ItemType.PRODUCT)) {
            confirmProductStock(impUid, merchantUid);
        } else {
            confirmFarmStock(impUid, merchantUid);
        }
    }


    @Override
    @Description("웹훅 처리 service -> 결제 정상적으로 성공됨을 보장: 결제 금액 확인")
    public void handlePortOneProductWebhookEvent(PaymentWebHookRequest paymentWebHookRequest) {
        String impUid = paymentWebHookRequest.impUid();
        String merchantUid = paymentWebHookRequest.merchantUid();

        if (paymentUtil.checkItemType(merchantUid).equals(ItemType.PRODUCT)) {
            handleProductPayment(impUid, merchantUid);
        } else {
            handleFarmPayment(impUid, merchantUid);
        }
    }

    private void handleProductPayment(String impUid, String merchantUid) {
        Order order = orderService.findByMerchantUid(merchantUid);
        BigDecimal amountToBePaid = order.getTotalAmount();

        if (paymentUtil.validatePaymentAmount(impUid, amountToBePaid)) {
            try {
                order.setPaymentComplete(impUid);
                orderService.save(order);
            } catch (BusinessException businessException) {
                throw new ApplicationException(PAYMENT_BAD_REQUEST);
            }
        } else {
            //실제 결제 된 금액과 결제 되어야 할 금액이 다르다면 -> 결제 취소 api를 호출해야 한다.
            paymentUtil.cancelPaymentByImpUid(impUid);
            order.cancel();
            orderService.save(order);
            throw new ApplicationException(PAYMENT_AMOUNT_MISMATCH);
        }

    }

    private void handleFarmPayment(String impUid, String merchantUid) {
        Reservation reservation = reservationService.findByMerchantUid(merchantUid);

        BigDecimal amountToBePaid = reservation.getPrice();

        if (paymentUtil.validatePaymentAmount(impUid, amountToBePaid)) {
            try {
                reservation.completePayment(impUid);
                reservationService.save(reservation);
            } catch (BusinessException businessException) {
                throw new ApplicationException(PAYMENT_BAD_REQUEST);
            }
        } else {
            //실제 결제 된 금액과 결제 되어야 할 금액이 다르다면 -> 결제 취소 api를 호출해야 한다.
            paymentUtil.cancelPaymentByImpUid(impUid);
            reservation.cancel();
            reservationService.save(reservation);
            throw new ApplicationException(PAYMENT_AMOUNT_MISMATCH);
        }
    }

    private void confirmProductStock(String impUid, String merchantUid) {
        Order order = orderService.findByMerchantUid(merchantUid);
        List<OrderedProduct> orderedProductList = order.getOrderedProducts();
        //수량 검증
        for (OrderedProduct orderedProduct : orderedProductList) {
            Product product = orderedProduct.getProduct();
            Integer remainQuantity = product.getStock();
            Integer orderQuantity = orderedProduct.getCount();

            //주문 재고가 남은 재고보다 많다면 500 + cancelReason 보내야 함
            if (orderQuantity > remainQuantity) {
                throw new PaymentConfirmException(PaymentConfirmError.PAYMENT_PROUCT_CONFIRM_EXCEPTION);
            }
        }
    }

    private void confirmFarmStock(String impUid, String merchantUid) {
        Reservation reservation = reservationService.findByMerchantUid(merchantUid);

        // FIXME: SQS로 웹훅 수신 여부 체크하는 로직으로 변경 필요 2024-11-13
    }

    @Description("결제 내역 단건 조회")
    public String getPayment(String impUid) {
        IamportResponse<com.siot.IamportRestClient.response.Payment> response = null;
        try {
            response = iamportClient.paymentByImpUid(impUid);
        } catch (IamportResponseException | IOException e) {
            throw new BusinessException(BusinessError.SQS_ERROR);
        };
        if(response.getCode() != 200)
            throw new BusinessException(BusinessError.SQS_ERROR);

        return response.getResponse().getStatus();
    }

    public void confirmProductPayment(Order productOrder, String status) {
        if(status.equals("paid") &&
                productOrder.getPayment().getPaymentStatus()== PAYMENT_PENDING){
            productOrder.getPayment().setPaymentStatus(PAYMENT_COMPLETE);
        }else if(status.equals("cancelled") || status.equals("failed")){
            productOrder.getPayment().setPaymentStatus(PAYMENT_DECLINED);
            List<OrderedProduct> products = productOrder.getOrderedProducts();

            products.forEach(orderedProduct->
                    orderedProduct.getProduct()
                            .addStock(orderedProduct.getCount()));
        }
    }

    public void confirmFarmPayment(Reservation reservation, String status) {
        if(status.equals("paid") &&
                reservation.getPayment().getPaymentStatus()== PAYMENT_PENDING){
            reservation.getPayment().setPaymentStatus(PAYMENT_COMPLETE);
        }else if(status.equals("cancelled") || status.equals("failed")){
            reservation.getPayment().setPaymentStatus(PAYMENT_DECLINED);
        }
    }


    @Transactional
    public void cancelExpiredPendingPayments() {
        List<Payment> payments = paymentRepository.findPendingPaymentsOlderThan(LocalDateTime.now().minusMinutes(10));
        for (Payment payment : payments) {

            if (payment.getOrder() != null) {
                List<OrderedProduct> orderedProducts = payment.getOrder().getOrderedProducts();
                for(OrderedProduct orderedProduct : orderedProducts){
                    Integer cancelRequestQuantity = orderedProduct.getCount();
                    orderedProduct.getProduct().addStock(cancelRequestQuantity);
                    orderedProduct.setOrderedProductStatus(PAYMENT_CANCELLED);
                }
            }
            if (payment.getReservation() != null) {
                Reservation reservation = payment.getReservation();
                reservationService.cancelReservation(reservation);
            }
            payment.setPaymentStatus(PAYMENT_DECLINED);
        }
    }

    public OrderResponse validateProductPayment(PaymentValidateRequest paymentValidateRequest){
        String merchantUid = paymentValidateRequest.merchantUid();
        BigDecimal amount = paymentValidateRequest.amount();

        Order order = orderService.validating(merchantUid, amount);
        OrderResponse orderResponse = OrderResponse.fromEntity(order);
        return orderResponse;
    }

    public ReservationResponse validateFarmPayment(PaymentValidateRequest PaymentValidateRequest){
        String merchantUid = PaymentValidateRequest.merchantUid();
        BigDecimal amount = PaymentValidateRequest.amount();

        Reservation reservation = reservationService.findByMerchantUid(merchantUid);
        Payment payment = reservation.getPayment();

        if(payment.getPaymentStatus()== PaymentStatus.PAYMENT_PENDING){
            throw new ApplicationException(PAYMENT_BAD_REQUEST);
        }

        if (payment.getTotalAmount().compareTo(amount) != 0) {
            throw new ApplicationException(PAYMENT_BAD_REQUEST);
        }

        ReservationResponse reservationResponse = reservation.toResponse();

        return reservationResponse;
    }

}


