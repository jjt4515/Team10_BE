package poomasi.payment.dto.request;

import java.math.BigDecimal;

public record PaymentValidateRequest(String merchantUid, BigDecimal amount) {
}
