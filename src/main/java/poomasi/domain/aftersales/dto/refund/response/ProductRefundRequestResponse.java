package poomasi.domain.aftersales.dto.refund.response;

import poomasi.domain.aftersales.entity._product.ProductAfterSalesStatus;
import poomasi.domain.order.entity.OrderedProductStatus;

import java.math.BigDecimal;

public record ProductRefundRequestResponse(
        Long orderedProductId,
        OrderedProductStatus orderedProductStatus,

        Long productAfterSalesDetailId,
        Integer refundQuantity,
        ProductAfterSalesStatus productAfterSalesTypem,
        BigDecimal finalRefundAmount
) {
}


