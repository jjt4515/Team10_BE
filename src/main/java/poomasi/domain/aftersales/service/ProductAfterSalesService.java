package poomasi.domain.aftersales.service;


import com.siot.IamportRestClient.exception.IamportResponseException;
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
import poomasi.domain.aftersales.dto.refund.request.ProductRefundRequestDeniedRequest;
import poomasi.domain.aftersales.dto.refund.response.ProductRefundRequestApprovalResponse;
import poomasi.domain.aftersales.dto.refund.response.ProductRefundRequestDeniedResponse;
import poomasi.domain.aftersales.dto.refund.response.ProductRefundRequestResponse;
import poomasi.domain.aftersales.entity._product.ProductAfterSalesDetail;
import poomasi.domain.aftersales.entity._product.ProductAfterSalesStatus;
import poomasi.domain.aftersales.entity._product.ProductRefundDetail;
import poomasi.domain.aftersales.repository.ProductAfterSalesDetailRepository;
import poomasi.domain.aftersales.repository.ProductRefundDetailRepository;
import poomasi.domain.auth.security.userdetail.UserDetailsImpl;
import poomasi.domain.member.entity.Member;
import poomasi.domain.order.entity.Order;
import poomasi.domain.order.entity.OrderedProduct;
import poomasi.domain.order.entity.OrderedProductStatus;
import poomasi.domain.order.repository.OrderedProductRepository;
import poomasi.domain.order.service.OrderService;
import poomasi.domain.reservation.service.ReservationService;
import poomasi.global.error.ApplicationException;
import poomasi.global.error.BusinessException;
import poomasi.payment.entity.Payment;
import poomasi.payment.util.PaymentUtil;

import java.io.IOException;
import java.math.BigDecimal;

import static poomasi.domain.order.entity.OrderedProductStatus.DELIVERED;
import static poomasi.domain.order.entity.OrderedProductStatus.PENDING_SELLER_APPROVAL;
import static poomasi.global.error.ApplicationError.PAYMENT_CHECKSUM_EXCESSIVE_REFUND_AMOUNT;
import static poomasi.global.error.BusinessError.*;

@Service
@RequiredArgsConstructor
public class ProductAfterSalesService implements CancelService<ProductCancelResponse, ProductCancelRequest>{

    private final OrderedProductRepository orderedProductRepository;
    private final PaymentUtil paymentUtil;
    private final ProductAfterSalesDetailRepository productAfterSalesDetailRepository;
    private final ProductRefundDetailRepository productRefundDetailRepository;
    private final OrderService orderService;
    private final ReservationService reservationService;

    @Override
    @Transactional
    public ProductCancelResponse cancel(ProductCancelRequest productCancelRequest){

        Long orderedProductId = productCancelRequest.orderedProductId();
        Long memberId = getMember().getId();

        //취소 가능한지 체크
        OrderedProduct orderedProduct = orderService.validateProductCancelable(memberId, orderedProductId);

        //포트원 취소를 위한 주문 Id 찾기
        Payment payment = orderedProduct.getPayment();
        Order order = orderedProduct.getOrder();
        String impUid = payment.getImpUid();
        
        //최종 취소 될 금액 계산 -> 배송비는 처음 한 번 환불
        BigDecimal finalCancelAmount = orderedProduct.calculateCancelAmount();

        // 체크섬 검증
        BigDecimal checkSum = payment.getCheckSum();

        //취소 요청 후, 주문 취소 상태로 변경
        paymentUtil.partialRefundByImpUid(impUid, checkSum, finalCancelAmount);
        payment.subtractCheckSum(finalCancelAmount);

        // 취소 수량 증가
        Integer cancelRequestQuantity = orderedProduct.getCount();
        orderedProduct.getProduct().addStock(cancelRequestQuantity);

        //취소 내역 저장
        ProductAfterSalesDetail productAfterSalesDetail = new ProductAfterSalesDetail()
                .builder()
                .orderedProduct(orderedProduct)
                .productAfterSalesStatus(ProductAfterSalesStatus.CANCEL)
                .build();
        orderedProduct.addProductAfterSalesDetail(productAfterSalesDetail);

        productAfterSalesDetailRepository.save(productAfterSalesDetail);
        //응답 반환
        return new ProductCancelResponse(
                orderedProductId,
                orderedProduct.getOrderedProductStatus(),
                productAfterSalesDetail.getId(),
                cancelRequestQuantity,
                productAfterSalesDetail.getProductAfterSalesStatus(),
                finalCancelAmount
        );
    }

    //-------------------------refund---------------------//

    @Description("환불 요청하는 메서드")
    @Transactional
    public ProductRefundRequestResponse createRefundRequest(ProductRefundRequest productRefundRequest) {
        Long orderedProductId = productRefundRequest.orderedProductId();
        Long memberId = getMember().getId();

        // 주인 검증 - 유저의 orderedProductId가 맞는지 검증
        OrderedProduct orderedProduct = orderService.validateProductRefundable(memberId, orderedProductId);

        Payment payment = orderedProduct.getPayment();
        Order order = orderedProduct.getOrder();
        String impUid = payment.getImpUid();

        OrderedProductStatus orderedProductStatus = orderedProduct.getOrderedProductStatus();
        if(orderedProductStatus == DELIVERED){
            throw new BusinessException(PURCHASE_ALREADY_CONFIRMED);
        }

        //최종 환불 금액 계산
        BigDecimal finalRefundAmount = orderedProduct.calculateRefundAmount();

        // 체크섬 검증
        BigDecimal checkSum = payment.getCheckSum();

        //취소 요청 후, 주문 취소 상태로 변경
        payment.subtractCheckSum(finalRefundAmount);
        paymentUtil.partialRefundByImpUid(impUid, checkSum, finalRefundAmount);

        //환불 내역 저장
        ProductAfterSalesDetail productAfterSalesDetail = new ProductAfterSalesDetail()
                .builder()
                .orderedProduct(orderedProduct)
                .productAfterSalesStatus(ProductAfterSalesStatus.REFUND_REQUESTED)
                .build();

        //환불 상세 저장
        orderedProduct.addProductAfterSalesDetail(productAfterSalesDetail);

        productAfterSalesDetailRepository.save(productAfterSalesDetail);

        //응답 반환
        return new ProductRefundRequestResponse(
                orderedProductId,
                orderedProductStatus,
                productAfterSalesDetail.getId(),
                productAfterSalesDetail.getProductAfterSalesStatus(),
                finalRefundAmount
        );
    }


    @Description("판매자 환불 확인 메서드")
    @Transactional
    public ProductRefundRequestDeniedResponse processRefundApproval(ProductRefundRequestApprovalRequest productRefundRequestApprovalRequest){
        Long productAfterSalesDetailId = productRefundRequestApprovalRequest.productAfterSalesDetailId();

        //환불 요청이 존재하는지 그리고 자신의 환불 요청인지 검증하고
        ProductAfterSalesDetail productAfterSalesDetail = validateProductRefundRequestByFarmerId(productAfterSalesDetailId);

        //전달한다
        return new ProductRefundRequestDeniedResponse(
                productAfterSalesDetail.getId(),
                productAfterSalesDetail.getProductAfterSalesStatus()
        );
    }


    @Description("환불 요청이 존재하고, 판매자 소유인지 확인하는 메서드 ")
    private ProductAfterSalesDetail validateProductRefundRequestByFarmerId(Long productAfterSalesDetailId){
        ProductAfterSalesDetail productAfterSalesDetail = productAfterSalesDetailRepository.findById(productAfterSalesDetailId)
                .orElseThrow(()-> new BusinessException(REFUND_AFTER_SALES_NOT_FOUND)
                );
        Long farmerId = getMember().getId();
        /*
        if(farmerId != productAfterSalesDetail.getOrderedProduct().getStoreId().getMember()){
            throw new BusinessException(REFUND_AFTER_SALES_REQUEST_INVALID_OWNER);
        }
        */
        return productAfterSalesDetail;
    }

    // ------------------------------//
    @Description("security context에서 member 객체 가져오는 메서드")
    private Member getMember() {
        Authentication authentication = SecurityContextHolder
                .getContext().getAuthentication();
        Object impl = authentication.getPrincipal();
        Member member = ((UserDetailsImpl) impl).getMember();
        return member;
    }

}
