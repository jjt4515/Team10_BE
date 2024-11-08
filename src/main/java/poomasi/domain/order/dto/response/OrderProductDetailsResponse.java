package poomasi.domain.order.dto.response;

import poomasi.domain.order.entity.OrderProductDetails;
import poomasi.domain.product.dto.ProductResponse;

import java.math.BigDecimal;

public record OrderProductDetailsResponse(
        Long orderId,
        Long orderProductDetailsId,
        Long productId,
        String productName,
        Integer count,
        BigDecimal price, //총 결제 금액
        String invoiceNumber
        ) {
    public static OrderProductDetailsResponse fromEntity(OrderProductDetails orderProductDetails) {
        return new OrderProductDetailsResponse(
                orderProductDetails.getOrder().getId(),
                orderProductDetails.getId(),
                orderProductDetails.getProduct().getId(),
                orderProductDetails.getProductName(),
                orderProductDetails.getCount(),
                orderProductDetails.getPrice(),
                orderProductDetails.getInvoiceNumber()
        );
    }

}
