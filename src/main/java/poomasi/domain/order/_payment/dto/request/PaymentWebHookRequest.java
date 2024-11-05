package poomasi.domain.order._payment.dto.request;

public record PaymentWebHookRequest(String imp_uid,
                                    String merchant_uid) {
}
