package poomasi.domain.order._aftersales.dto;

import java.math.BigDecimal;

public record PartialRefundRequest(
        BigDecimal refundAmount, // type check 필요
        String refundReason
) {
}
