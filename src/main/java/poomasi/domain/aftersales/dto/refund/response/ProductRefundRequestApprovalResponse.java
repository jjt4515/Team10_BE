package poomasi.domain.aftersales.dto.refund.response;

import java.math.BigDecimal;

public record ProductRefundRequestApprovalResponse(
        Long orderedProductId,
        Integer count,
        BigDecimal refundAmount,
        Long productAfterSalesDetailId,
        String invoiceNumber
) {
}
