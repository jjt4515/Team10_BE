package poomasi.domain.order.dto.response;

import java.math.BigDecimal;
import poomasi.domain.order.entity.OrderedProduct;

public record OrderProductDetailsResponse(
        Long orderId,
        Long orderProductDetailsId,
        Long productId,
        String productName,
        Integer count,
        BigDecimal price, //총 결제 금액
        String invoiceNumber,
        boolean isReviewed
) {

    public static OrderProductDetailsResponse fromEntity(OrderedProduct orderedProduct) {
        return new OrderProductDetailsResponse(
                orderedProduct.getOrderId(),
                orderedProduct.getId(),
                orderedProduct.getProduct().getId(),
                orderedProduct.getProductName(),
                orderedProduct.getCount(),
                orderedProduct.getPrice(),
                orderedProduct.getInvoiceNumber(),
                orderedProduct.getReview() != null
        );
    }

}
