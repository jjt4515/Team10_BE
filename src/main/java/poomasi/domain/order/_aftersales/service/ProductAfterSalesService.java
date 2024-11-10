package poomasi.domain.order._aftersales.service;


import com.siot.IamportRestClient.exception.IamportResponseException;
import jdk.jfr.Description;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import poomasi.domain.auth.security.userdetail.UserDetailsImpl;
import poomasi.domain.member.entity.Member;
import poomasi.domain.order._aftersales.dto.cancel.request.ProductCancelRequest;
import poomasi.domain.order._aftersales.dto.exchange.request.ProductExchangeRequest;
import poomasi.domain.order._aftersales.dto.cancel.response.ProductCancelResponse;
import poomasi.domain.order._aftersales.dto.exchange.response.ProductExchangeResponse;
import poomasi.domain.order._aftersales.entity._product.ProductAfterSalesDetail;
import poomasi.domain.order._aftersales.repository.ProductAfterSalesDetailRepository;
import poomasi.domain.order._payment.util.PaymentUtil;
import poomasi.domain.order.entity._product.OrderedProduct;
import poomasi.domain.order.entity._product.ProductOrder;
import poomasi.domain.order.entity._product.OrderedProductStatus;
import poomasi.domain.order.repository.OrderedProductRepository;
import poomasi.global.error.BusinessException;

import java.io.IOException;
import java.math.BigDecimal;

import static poomasi.domain.order._aftersales.entity._product.ProductAfterSalesType.CANCEL;
import static poomasi.domain.order.entity._product.OrderedProductStatus.*;
import static poomasi.global.error.BusinessError.*;

@Service
@RequiredArgsConstructor
public class ProductAfterSalesService {

    private final OrderedProductRepository orderedProductRepository;
    private final PaymentUtil paymentUtil;
    private final ProductAfterSalesDetailRepository productAfterSalesDetailRepository;

    //-------------------------cancel---------------------//

    @Description("판매자 확인 전 취소하는 메서드. 판매자 확인 대기 전 경우만 취소 할 수 있음")
    public ProductCancelResponse productCancelBeforeSellerApproval(ProductCancelRequest productCancelRequest) throws IOException, IamportResponseException {

        Long orderedProductId = productCancelRequest.orderedProductId();
        OrderedProduct orderedProduct = validateProductRequestByMemberId(orderedProductId);
        ProductOrder productOrder = orderedProduct.getProductOrder();
        String impUid = productOrder.getImpUid();
        BigDecimal amoutToBeCancel = orderedProduct.getPrice();
        String cancelReason = productCancelRequest.reason();
        OrderedProductStatus orderedProductStatus = orderedProduct.getOrderedProductStatus();
        
        //판매자 확인 대기 전이 아니라면 (잘못 된 요청)
        if(orderedProductStatus != PENDING_SELLER_APPROVAL){
            throw new BusinessException(SHIPPING_ALREADY_IN_PROGRESS);
        }
        
        //취소 요청 후
        paymentUtil.cancelPaymentByImpUid(impUid); 
        
        //주문 취소 상태로 변경
        orderedProduct.setOrderedProductStatus(CANCELLED);

        ProductAfterSalesDetail productAfterSalesDetail = new ProductAfterSalesDetail()
                .builder()
                .orderedProduct(orderedProduct)
                .amount(amoutToBeCancel)
                .reason(cancelReason)
                .productAfterSalesType(CANCEL)
                .build();

        orderedProduct.addProductAfterSalesDetail(productAfterSalesDetail);
        productAfterSalesDetailRepository.save(productAfterSalesDetail);

        return new ProductCancelResponse(orderedProductId, orderedProductStatus);
    }



    @Description("security context에서 member 객체 가져오는 메서드")
    private Member getMember() {
        Authentication authentication = SecurityContextHolder
                .getContext().getAuthentication();
        Object impl = authentication.getPrincipal();
        Member member = ((UserDetailsImpl) impl).getMember();
        return member;
    }

    @Description("요청이 멤버 소유인지 확인하는 메서드")
    private OrderedProduct validateProductRequestByMemberId(Long orderedProductId){
        Member member = getMember();
        Long memberId = getMember().getId();
        OrderedProduct orderedProduct = orderedProductRepository.findByIdAndMemberId(orderedProductId, memberId)
                .orElseThrow(()-> new BusinessException(ORDERED_PRODUCT_NOT_FOUND));
        return orderedProduct;
    }


}
