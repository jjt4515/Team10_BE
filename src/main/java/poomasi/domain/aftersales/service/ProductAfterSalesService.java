package poomasi.domain.aftersales.service;


import jdk.jfr.Description;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poomasi.domain.aftersales.dto.cancel.request.ProductCancelRequest;
import poomasi.domain.aftersales.dto.cancel.response.ProductCancelResponse;
import poomasi.domain.aftersales.dto.refund.request.ProductRefundRequest;
import poomasi.domain.aftersales.dto.refund.request.ProductRefundRequestApprovalRequest;
import poomasi.domain.aftersales.dto.refund.response.ProductRefundRequestApprovalResponse;
import poomasi.domain.aftersales.dto.refund.response.ProductRefundResponse;
import poomasi.domain.aftersales.entity.ProductAfterSales;
import poomasi.domain.aftersales.repository.ProductAfterSalesRepository;
import poomasi.domain.auth.security.userdetail.UserDetailsImpl;
import poomasi.domain.member.entity.Member;
import poomasi.domain.order.entity.OrderedProduct;
import poomasi.domain.order.entity.OrderedProductStatus;
import poomasi.domain.order.repository.OrderedProductRepository;
import poomasi.domain.order.service.OrderService;
import poomasi.domain.reservation.service.ReservationService;
import poomasi.global.error.ApplicationException;
import poomasi.global.error.BusinessException;
import poomasi.payment.entity.Payment;
import poomasi.payment.util.PaymentUtil;

import java.math.BigDecimal;

import static poomasi.domain.order.entity.OrderedProductStatus.CANCEL_PENDING;
import static poomasi.domain.order.entity.OrderedProductStatus.REFUND_APPROVED;
import static poomasi.global.error.ApplicationError.PAYMENT_CHECKSUM_EXCESSIVE_REFUND_AMOUNT;
import static poomasi.global.error.BusinessError.REFUND_AFTER_SALES_NOT_FOUND;
import static poomasi.global.error.BusinessError.REFUND_AFTER_SALES_REQUEST_INVALID_OWNER;

@Service
@RequiredArgsConstructor
public class ProductAfterSalesService implements CancelService<ProductCancelResponse, ProductCancelRequest>{

    private final OrderedProductRepository orderedProductRepository;
    private final PaymentUtil paymentUtil;
    private final OrderService orderService;
    private final ReservationService reservationService;
    private final ProductAfterSalesRepository productAfterSalesDetail;
    private final ProductAfterSalesRepository productAfterSalesRepository;

    @Override
    @Transactional
    public ProductCancelResponse cancel(ProductCancelRequest productCancelRequest){

        Long orderedProductId = productCancelRequest.orderedProductId();
        Long memberId = getMember().getId();

        //취소 가능한지 체크
        OrderedProduct orderedProduct = orderService.validateProductCancelable(memberId, orderedProductId);

        //포트원 취소를 위한 주문 Id 찾기
        Payment payment = orderedProduct.getPayment();
        String impUid = payment.getImpUid();

        BigDecimal finalCancelAmount = orderedProduct.calculateCancelAmount();

        // 체크섬 검증
        BigDecimal checkSum = payment.getCheckSum();
        if(!payment.isCheckSumValid(finalCancelAmount)){
            throw new ApplicationException(PAYMENT_CHECKSUM_EXCESSIVE_REFUND_AMOUNT);
        }

        //취소 요청 후, 주문 취소 상태로 변경
        paymentUtil.partialRefundByImpUid(impUid, checkSum, finalCancelAmount);

        // 취소 수량 증가
        Integer cancelRequestQuantity = orderedProduct.getCount();
        orderedProduct.getProduct().addStock(cancelRequestQuantity);
        orderedProduct.setOrderedProductStatus(CANCEL_PENDING);

        orderedProductRepository.save(orderedProduct);

        return new ProductCancelResponse(
                orderedProductId,
                orderedProduct.getOrderedProductStatus(),
                finalCancelAmount
        );
    }

    //-------------------------refund---------------------//
    @Description("환불 요청하는 메서드")
    @Transactional
    public ProductRefundResponse refund(ProductRefundRequest productRefundRequest) {
        Long orderedProductId = productRefundRequest.orderedProductId();
        Long memberId = getMember().getId();

        // 주인 검증 - 유저의 orderedProductId가 맞는지 검증
        OrderedProduct orderedProduct = orderService.validateProductRefundable(memberId, orderedProductId);

        Payment payment = orderedProduct.getPayment();
        String impUid = payment.getImpUid();

        OrderedProductStatus orderedProductStatus = orderedProduct.getOrderedProductStatus();
        BigDecimal finalRefundAmount = orderedProduct.calculateRefundAmount();

        // 체크섬 검증
        BigDecimal checkSum = payment.getCheckSum();
        if(payment.isCheckSumValid(finalRefundAmount)){
            throw new ApplicationException(PAYMENT_CHECKSUM_EXCESSIVE_REFUND_AMOUNT);
        }

        //취소 요청 후, 주문 취소 상태로 변경
        paymentUtil.partialRefundByImpUid(impUid, checkSum, finalRefundAmount);

        //응답 반환
        return new ProductRefundResponse(
                orderedProductId,
                orderedProductStatus,
                finalRefundAmount
        );
    }

    @Description("판매자 환불 확인 메서드")
    @Transactional
    public ProductRefundRequestApprovalResponse processRefundApproval(ProductRefundRequestApprovalRequest productRefundRequestApprovalRequest){
        Long productAfterSalesId = productRefundRequestApprovalRequest.productAfterSalesId();

        //환불 요청이 존재하는지 그리고 자신의 환불 요청인지 검증하고
        ProductAfterSales productAfterSales= validateProductRefundRequestByFarmerId(productAfterSalesId);
        productAfterSales.getOrderedProduct().setOrderedProductStatus(REFUND_APPROVED);

        //전달한다
        return new ProductRefundRequestApprovalResponse(
                productAfterSales.getId(),
                productAfterSales.getOrderedProduct().getOrderedProductStatus(),
                productAfterSales.getAfterSalesAmount()
        );
    }

    @Description("환불 요청이 존재하고, 판매자 소유인지 확인하는 메서드")
    private ProductAfterSales validateProductRefundRequestByFarmerId(Long productAfterSalesDetailId){
        ProductAfterSales productAfterSales = productAfterSalesRepository.findById(productAfterSalesDetailId)
                .orElseThrow(()-> new BusinessException(REFUND_AFTER_SALES_NOT_FOUND));
        Long farmerId = getMember().getId();

        if(farmerId != productAfterSales.getOrderedProduct().getStoreOwner().getId()){
            throw new BusinessException(REFUND_AFTER_SALES_REQUEST_INVALID_OWNER);
        }

        return productAfterSales;
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
