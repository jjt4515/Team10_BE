package poomasi.domain.order._aftersales.service;


import com.siot.IamportRestClient.exception.IamportResponseException;
import jdk.jfr.Description;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poomasi.domain.auth.security.userdetail.UserDetailsImpl;
import poomasi.domain.member.entity.Member;
import poomasi.domain.order._aftersales.dto.cancel.request.ProductCancelRequest;
import poomasi.domain.order._aftersales.dto.cancel.response.ProductCancelResponse;
import poomasi.domain.order._aftersales.dto.refund.request.ProductRefundRequest;
import poomasi.domain.order._aftersales.dto.refund.request.ProductRefundRequestApprovalRequest;
import poomasi.domain.order._aftersales.dto.refund.request.ProductRefundRequestDeniedRequest;
import poomasi.domain.order._aftersales.dto.refund.response.ProductRefundRequestApprovalResponse;
import poomasi.domain.order._aftersales.dto.refund.response.ProductRefundRequestDeniedResponse;
import poomasi.domain.order._aftersales.dto.refund.response.ProductRefundRequestResponse;
import poomasi.domain.order._aftersales.entity._product.ProductAfterSalesDetail;
import poomasi.domain.order._aftersales.entity._product.ProductAfterSalesStatus;
import poomasi.domain.order._aftersales.entity._product.ProductRefundDetail;
import poomasi.domain.order._aftersales.repository.ProductAfterSalesDetailRepository;
import poomasi.domain.order._aftersales.repository.ProductRefundDetailRepository;
import poomasi.domain.order.entity._product.OrderedProduct;
import poomasi.domain.order.entity._product.OrderedProductStatus;
import poomasi.domain.order.entity._product.ProductOrder;
import poomasi.domain.order.entity._product.ProductOrderDetails;
import poomasi.domain.order.repository.OrderedProductRepository;
import poomasi.global.error.ApplicationException;
import poomasi.global.error.BusinessException;
import poomasi.payment.util.PaymentUtil;

import java.io.IOException;
import java.math.BigDecimal;

import static poomasi.domain.order._aftersales.entity._product.ProductAfterSalesStatus.CANCEL;
import static poomasi.domain.order.entity._product.OrderedProductStatus.DELIVERED;
import static poomasi.domain.order.entity._product.OrderedProductStatus.PENDING_SELLER_APPROVAL;
import static poomasi.global.error.ApplicationError.PAYMENT_CHECKSUM_EXCESSIVE_REFUND_AMOUNT;
import static poomasi.global.error.BusinessError.*;

@Service
@RequiredArgsConstructor
public class ProductAfterSalesService implements CancelService<ProductCancelResponse, ProductCancelRequest>{

    private final OrderedProductRepository orderedProductRepository;
    private final PaymentUtil paymentUtil;
    private final ProductAfterSalesDetailRepository productAfterSalesDetailRepository;
    private final ProductRefundDetailRepository productRefundDetailRepository;

    //-------------------------cancel---------------------//
    @Description("판매자 확인 전 취소하는 메서드. 판매자 확인 대기 전 경우만 취소 할 수 있음")
    @Override
    @Transactional
    public ProductCancelResponse cancel(ProductCancelRequest productCancelRequest) throws IOException, IamportResponseException {

        Long orderedProductId = productCancelRequest.orderedProductId();
        String cancelReason = productCancelRequest.cancelReason();

        //주인 검증 - 유저의 orderedProductId가 맞는지 검증
        OrderedProduct orderedProduct = validateProductCancelRequestByMemberId(orderedProductId);

        //수량 검증
        Integer cancelRequestQuantity = productCancelRequest.cancelRequestQuantity();
        Integer adjustableQuantity = orderedProduct.getAdjustableQuantity();
        if(cancelRequestQuantity > adjustableQuantity){
            throw new BusinessException(CANCEL_QUANTITY_EXCEEDED);
        }

        //포트원 취소를 위한 주문 Id 찾기
        ProductOrder productOrder = orderedProduct.getProductOrder();
        String impUid = productOrder.getImpUid();

        //판매자 확인 대기 전이 아니라면 주문 취소를 할 수 없다
        OrderedProductStatus orderedProductStatus = orderedProduct.getOrderedProductStatus();
        if(orderedProductStatus != PENDING_SELLER_APPROVAL){
            throw new BusinessException(SHIPPING_ALREADY_IN_PROGRESS);
        }

        //최종 취소 될 금액 계산 -> 배송비는 처음 한 번 환불
        BigDecimal finalCancelAmount = calculateCancelAmount(orderedProduct, cancelRequestQuantity);

        //배송비 환불 플래그 설정
        
        // checksum 검증
        BigDecimal checkSum = productOrder.getCheckSum();

        //취소하려는 금액이 남은 환불 가능한 금액보다 크다면
        if(finalCancelAmount.compareTo(checkSum) > 0){
            throw new ApplicationException(PAYMENT_CHECKSUM_EXCESSIVE_REFUND_AMOUNT);
        }

        //취소 요청 후, 주문 취소 상태로 변경
        paymentUtil.partialRefundByImpUid(impUid, checkSum, finalCancelAmount, cancelReason);
        //orderedProduct.setOrderedProductStatus(CANCEL_PENDING);

        //checksum 뺴기 : 주문 취소가 정상적으로 완료가 되었다면 동기화
        productOrder.subtractChecksum(finalCancelAmount);
        
        //취소/환불/교환 가능 수량 변경 및 플래그 설정
        orderedProduct.subtractRefundableCount(cancelRequestQuantity);
        orderedProduct.addCancelQuantity(cancelRequestQuantity);
        //모두 취소 해버렸다면
        orderedProductStatus = orderedProduct.changeOrderedProductStatusToCancel();

        //TODO : 취소 된 수량도 추가해야 하나? 오늘 회의에서 결정함
        //취소 된 상품 수량 증가
        orderedProduct.getProduct().addStock(cancelRequestQuantity);

        //취소 내역 저장
        ProductAfterSalesDetail productAfterSalesDetail = new ProductAfterSalesDetail()
                .builder()
                .orderedProduct(orderedProduct)
                .adjustAmount(finalCancelAmount)
                .reason(cancelReason)
                .adjustmentQuantity(cancelRequestQuantity)
                .productAfterSalesStatus(CANCEL)
                .build();
        orderedProduct.addProductAfterSalesDetail(productAfterSalesDetail);
        productAfterSalesDetailRepository.save(productAfterSalesDetail);

        //응답 반환
        return new ProductCancelResponse(orderedProductId,
                orderedProductStatus,
                productAfterSalesDetail.getId(),
                cancelRequestQuantity,
                productAfterSalesDetail.getProductAfterSalesStatus(),
                finalCancelAmount
        );

    }

    @Description("요청이 구매자 소유인지 확인하는 메서드")
    private OrderedProduct validateProductCancelRequestByMemberId(Long orderedProductId){
        Member member = getMember();
        Long memberId = getMember().getId();
        OrderedProduct orderedProduct = orderedProductRepository.findById(orderedProductId)
                .orElseThrow(()-> new BusinessException(ORDERED_PRODUCT_NOT_FOUND));

        if(orderedProduct.getProductOrder().getMember().getId() != memberId){
            new BusinessException(ORDERED_PRODUCT_NOT_FOUND); // TODO : 메서드 추출 이후, error enum 변경
        }

        return orderedProduct;
    }


    @Description("취소 요청에서 취소 금액 계산하는 메서드. 취소 전적이 한 번이라도 있으면 배송비 환불 x.")
    private BigDecimal calculateCancelAmount(OrderedProduct orderedProduct, Integer cancelRequestQuantity){

        boolean isCanceled = orderedProduct.isCanceled();

        BigDecimal cancelAmount = orderedProduct.getPrice()
                .multiply(new BigDecimal(cancelRequestQuantity)
                );

        if(!isCanceled){
            //배송비 붙여야 한다
            BigDecimal deliveryFee = orderedProduct.getDeliveryFee();
            cancelAmount = cancelAmount.add(deliveryFee);
        }
        return cancelAmount;
    }


    //-------------------------refund---------------------//
    @Description("환불 요청하는 메서드")
    @Transactional
    public ProductRefundRequestResponse createRefundRequest(ProductRefundRequest productRefundRequest) {
        Long orderedProductId = productRefundRequest.orderedProductId();
        String refundReason = productRefundRequest.refundReason();

        // 주인 검증 - 유저의 orderedProductId가 맞는지 검증
        OrderedProduct orderedProduct = validateProductCancelRequestByMemberId(orderedProductId);

        // 수량 검증 - 조정 가능한 수량보다 환불 수량이 많으면 exception
        Integer refundRequestQuantity = productRefundRequest.refundRequestQuantity();
        Integer adjustableQuantity = orderedProduct.getAdjustableQuantity();
        if(refundRequestQuantity > adjustableQuantity){
            throw new BusinessException(REFUND_QUANTITY_EXCEEDED);
        }

        //포트원 취소를 위한 주문 Id 찾기
        ProductOrder productOrder = orderedProduct.getProductOrder();
        String impUid = productOrder.getImpUid();

        //구매 확정 상태라면 환불을 할 수 없다
        OrderedProductStatus orderedProductStatus = orderedProduct.getOrderedProductStatus();
        if(orderedProductStatus == DELIVERED){
            throw new BusinessException(PURCHASE_ALREADY_CONFIRMED);
        }

        // 배송 대기 전 상태라면 환불을 할 수 없다.
        if(orderedProductStatus == PENDING_SELLER_APPROVAL){
            throw new BusinessException(REFUND_NOT_ALLOWED_BEFORE_SHIPPING);
        }

        //최종 환불 금액 계산
        BigDecimal finalRefundAmount = calculateRefundAmount(orderedProduct, refundRequestQuantity);

        // checksum 검증
        BigDecimal checkSum = productOrder.getCheckSum();

        //취소하려는 금액이 남은 환불 가능한 금액보다 크다면
        if(finalRefundAmount.compareTo(checkSum) > 0){
            throw new ApplicationException(PAYMENT_CHECKSUM_EXCESSIVE_REFUND_AMOUNT);
        }

        //취소/환불/교환 가능 수량 변경
        orderedProduct.subtractRefundableCount(refundRequestQuantity);

        //환불 내역 저장
        ProductAfterSalesDetail productAfterSalesDetail = new ProductAfterSalesDetail()
                .builder()
                .orderedProduct(orderedProduct)
                .adjustAmount(finalRefundAmount)
                .reason(refundReason)
                .adjustmentQuantity(refundRequestQuantity)
                .productAfterSalesStatus(ProductAfterSalesStatus.REFUND_REQUESTED)
                .build();

        //환불 상세 저장
        ProductRefundDetail productRefundDetail = createProductRefundDetail(orderedProduct, productOrder, productRefundRequest);
        productAfterSalesDetail.setProductRefundDetail(productRefundDetail);
        orderedProduct.addProductAfterSalesDetail(productAfterSalesDetail);

        productAfterSalesDetailRepository.save(productAfterSalesDetail);
        productRefundDetailRepository.save(productRefundDetail);

        //응답 반환
        return new ProductRefundRequestResponse(
                orderedProductId,
                orderedProductStatus,
                productAfterSalesDetail.getId(),
                refundRequestQuantity,
                productAfterSalesDetail.getProductAfterSalesStatus(),
                finalRefundAmount
        );
    }

    @Description("반품 상세 요청사항 만드는 메서드")
    private ProductRefundDetail createProductRefundDetail(OrderedProduct orderedProduct,
                                                          ProductOrder productOrder,
                                                          ProductRefundRequest productRefundRequest) {

        ProductOrderDetails productOrderDetails = productOrder.getProductOrderDetails();
        String pickupLocationAddress = productOrderDetails.getDestinationAddress();
        String pickUpLocationAddressDetail = productOrderDetails.getDestinationAddressDetail();

        String returnAddress = orderedProduct.getStoreAddress();
        String returnAddressDetail = orderedProduct.getStoreAddressDetail();

        String request = productRefundRequest.request(); //nullable field

        ProductRefundDetail productRefundDetail = new ProductRefundDetail()
                .builder()
                .pickupLocationAddress(pickupLocationAddress)
                .pickupLocationAddressDetail(pickUpLocationAddressDetail)
                .returnAddress(returnAddress)
                .returnAddressDetail(returnAddressDetail)
                .request(request)
                .build();

        return productRefundDetail;
    }


    @Description("환불 요청에서 환불 금액 계산하는 메서드")
    private BigDecimal calculateRefundAmount(OrderedProduct orderedProduct, Integer refundRequestQuantity){

        BigDecimal refundAmount = orderedProduct.getPrice()
                .multiply(new BigDecimal(refundRequestQuantity));

        return refundAmount;
    }


    @Description("판매자 환불 거절 메서드")
    @Transactional
    public ProductRefundRequestDeniedResponse processRefundDenied(ProductRefundRequestDeniedRequest productRefundRequestDeniedRequest){

        Long productAfterSalesDetailId = productRefundRequestDeniedRequest.productAfterSalesDetailId();
        String refundDeinedReason = productRefundRequestDeniedRequest.refundDeinedReason();

        //환불 요청이 존재하는지 그리고 자신의 환불 요청인지 검증하고
        ProductAfterSalesDetail productAfterSalesDetail = validateProductRefundRequestByFarmerId(productAfterSalesDetailId);
        
        //refund detail 만든 후
        ProductRefundDetail productRefundDetail = productAfterSalesDetail.getProductRefundDetail();
        productRefundDetail.setProductRefundDeniedReason(refundDeinedReason);

        //환불 거부 상태로 등록 후
        productAfterSalesDetail.setProductAfterSalesStatus(ProductAfterSalesStatus.REFUND_DENIED);

        productAfterSalesDetail.setProductRefundDetail(productRefundDetail);
        //db에 저장한다
        productRefundDetailRepository.save(productRefundDetail);

        //전달한다
        return new ProductRefundRequestDeniedResponse(
                productAfterSalesDetail.getId(),
                productAfterSalesDetail.getProductAfterSalesStatus(),
                productAfterSalesDetail.getProductRefundDeniedReason()
        );
    }

    @Description("판매자 환불 확인 메서드")
    public ProductRefundRequestApprovalResponse processRefundApproval(ProductRefundRequestApprovalRequest productRefundRequestApprovalRequest) throws IOException, IamportResponseException {

        Long productAfterSalesDetailId = productRefundRequestApprovalRequest.productAfterSalesDetailId();
        String invoiceNumber = productRefundRequestApprovalRequest.invoiceNumber();

        //환불 요청이 존재하는지 그리고 자신의 환불 요청인지 검증하고
        ProductAfterSalesDetail productAfterSalesDetail = validateProductRefundRequestByFarmerId(productAfterSalesDetailId);

        //환불 결제 금액 찾고
        BigDecimal finalRefundAmount = productAfterSalesDetail.getAdjustAmount();

        //결제를 찾는다
        OrderedProduct orderedProduct = productAfterSalesDetail.getOrderedProduct();
        ProductOrder productOrder = orderedProduct.getProductOrder();

        //환불에 필요한 parameter
        String refundReason = productAfterSalesDetail.getReason();
        String impUid = productOrder.getImpUid();
        BigDecimal checkSum = productOrder.getCheckSum();
        
        //환불 처리
        paymentUtil.partialRefundByImpUid(impUid, checkSum, finalRefundAmount, refundReason);

        //성공하면 checksum 포트원 서버와 동기화
        productOrder.subtractChecksum(finalRefundAmount);
        
        //운송장 번호와 상태 등록
        productAfterSalesDetail.changeRefundApproveStatus(invoiceNumber);

        return new ProductRefundRequestApprovalResponse(
                orderedProduct.getId(),
                productAfterSalesDetail.getAdjustmentQuantity(),
                finalRefundAmount,
                productAfterSalesDetailId,
                invoiceNumber
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
