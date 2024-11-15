package poomasi.domain.aftersales.dto.refund.response;

import poomasi.domain.order.entity.OrderedProductStatus;

import java.math.BigDecimal;

public record ProductRefundRequestApprovalResponse(
        Long orderedProductId,
        OrderedProductStatus orderedProductStatus,
        BigDecimal refundAmount
) {
}
