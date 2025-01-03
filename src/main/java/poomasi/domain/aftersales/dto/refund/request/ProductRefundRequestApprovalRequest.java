package poomasi.domain.aftersales.dto.refund.request;

import java.math.BigDecimal;

public record ProductRefundRequestApprovalRequest(Long orderedProductId, BigDecimal refundAmount) {
}
