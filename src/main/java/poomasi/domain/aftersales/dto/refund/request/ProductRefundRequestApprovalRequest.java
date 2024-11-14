package poomasi.domain.aftersales.dto.refund.request;

public record ProductRefundRequestApprovalRequest(Long productAfterSalesDetailId,
                                                  String invoiceNumber) {
}
