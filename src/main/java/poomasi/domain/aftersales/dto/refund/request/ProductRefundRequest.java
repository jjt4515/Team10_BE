package poomasi.domain.aftersales.dto.refund.request;

import jakarta.validation.constraints.NotNull;

public record ProductRefundRequest(
        @NotNull Long orderedProductId
) {
}
