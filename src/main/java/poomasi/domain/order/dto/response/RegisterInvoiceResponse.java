package poomasi.domain.order.dto.response;

public record RegisterInvoiceResponse(Long orderedProductId, String deliveryService, String invoiceNumber) {
}
