package poomasi.domain.aftersales.dto.refund.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record ProductRefundRequest(
        @NotNull Long orderedProductId
) {
}
