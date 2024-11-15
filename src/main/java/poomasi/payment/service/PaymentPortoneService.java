package poomasi.payment.service;

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
import poomasi.global.error.BusinessException;
import poomasi.global.error.PaymentConfirmError;
import poomasi.global.error.PaymentConfirmException;
import poomasi.payment.dto.request.PaymentWebHookRequest;
import poomasi.payment.entity.ItemType;
import poomasi.payment.util.PaymentUtil;

import java.math.BigDecimal;
import java.util.List;

import static poomasi.global.error.ApplicationError.PAYMENT_AMOUNT_MISMATCH;
import static poomasi.global.error.ApplicationError.PAYMENT_BAD_REQUEST;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentPortoneService implements PaymentService {
    private final PaymentUtil paymentUtil;
    private final ProductService productService;
    private final OrderService orderService;
    private final ReservationService reservationService;

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
        //결제 되어야 할 금액
        BigDecimal amountToBePaid = order.getTotalAmount();
    }

    private void handleFarmPayment(String impUid, String merchantUid) {
        Reservation reservation = reservationService.findByMerchantUid(merchantUid);

        BigDecimal amountToBePaid = reservation.getPrice();

        if (paymentUtil.validatePaymentAmount(impUid, amountToBePaid)) {
            try {
                reservation.completePayment();
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


}


