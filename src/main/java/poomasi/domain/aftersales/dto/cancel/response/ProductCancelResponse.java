package poomasi.domain.aftersales.dto.cancel.response;

import poomasi.domain.order.entity.OrderedProductStatus;

import java.math.BigDecimal;

public record ProductCancelResponse(
        Long orderedProductId,
        OrderedProductStatus orderedProductStatus,
        BigDecimal finalCancelAmount,
        Long productAfterSalesId) {
}
