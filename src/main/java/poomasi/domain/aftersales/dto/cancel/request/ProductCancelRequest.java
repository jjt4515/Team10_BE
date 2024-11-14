package poomasi.domain.aftersales.dto.cancel.request;

public record ProductCancelRequest(Long orderedProductId, Integer cancelRequestQuantity, String cancelReason) {
}
