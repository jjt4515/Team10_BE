package poomasi.domain.aftersales.dto.cancel.response;

import poomasi.domain.aftersales.entity._product.ProductAfterSalesStatus;
import poomasi.domain.order.entity.OrderedProductStatus;

import java.math.BigDecimal;

public record ProductCancelResponse(
        Long orderedProductId,
        OrderedProductStatus orderedProductStatus,

        Long productAfterSalesDetailId,
        Integer cancelQuantity,
        ProductAfterSalesStatus productAfterSalesStatus,
        BigDecimal finalCancelAmount) {
}
