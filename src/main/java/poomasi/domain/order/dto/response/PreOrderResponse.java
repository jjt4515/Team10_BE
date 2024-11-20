package poomasi.domain.order.dto.response;

import java.math.BigDecimal;

public record PreOrderResponse(String merchantUid, BigDecimal amount) {
}
