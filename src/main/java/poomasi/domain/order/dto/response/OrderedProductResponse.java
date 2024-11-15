package poomasi.domain.order.dto.response;

import java.math.BigDecimal;
import poomasi.domain.order.entity.OrderedProduct;

public record OrderedProductResponse(
        Long productId,
        String productName,
        Integer count,
        String productDescription,
        BigDecimal price, //총 결제 금액
        String storeName,
        String invoiceStatus,
        String deliveryService,
        String invoiceNumber,
        boolean isReviewed
) {
    public static OrderedProductResponse fromEntity(OrderedProduct orderedProduct) {
        return new OrderedProductResponse(
                orderedProduct.getProductId(),
                orderedProduct.getProductName(),
                orderedProduct.getCount(),
                orderedProduct.getProductDescription(),
                orderedProduct.getPrice(),
                orderedProduct.getStoreName(),
                orderedProduct.getOrderedProductStatus().toString(),
                orderedProduct.getDeliveryService(),
                orderedProduct.getInvoiceNumber(),
                orderedProduct.getReview() != null
        );
    }

}
