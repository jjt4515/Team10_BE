package poomasi.domain.aftersales.dto.refund.response;

import poomasi.domain.aftersales.entity._product.ProductAfterSalesStatus;

public record ProductRefundRequestDeniedResponse(Long productAfterSalesDetailId,
                                                 ProductAfterSalesStatus productAfterSalesStatus,
                                                 String productRefundDeniedReason) {
}
