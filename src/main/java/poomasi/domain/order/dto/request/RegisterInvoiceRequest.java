package poomasi.domain.order.dto.request;

public record RegisterInvoiceRequest(Long orderedProductId, String deliveryService, String invoiceNumber) {
}
