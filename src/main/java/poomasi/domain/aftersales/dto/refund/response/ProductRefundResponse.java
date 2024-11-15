package poomasi.domain.aftersales.dto.refund.response;

import poomasi.domain.order.entity.OrderedProductStatus;

import java.math.BigDecimal;

public record ProductRefundResponse(
        Long orderedProductId,
        OrderedProductStatus orderedProductStatus,
        BigDecimal finalRefundAmount
) {
}


