package poomasi.global.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ApplicationError {
    ENCRYPT_ERROR("암호화 에러입니다."),

    // Payment
    PAYMENT_INVALID_REQUEST("결제 요청이 올바르지 않습니다."),
    PAYMENT_NOT_FOUND("결제를 찾을 수 없습니다."),
    PAYMENT_AMOUNT_MISMATCH("사전 결제 금액과 사후 결제 금액이 일치하지 않습니다."),
    PAYMENT_BAD_REQUEST("잘못 된 결제 요청입니다."),
    PAYMENT_CHECKSUM_EXCESSIVE_REFUND_AMOUNT("환불 요청 금액이 환불 가능한 금액보다 더 많습니다"),

    // OCR
    OCR_SUPPORT_ERROR("지원하지 않는 OCR 서비스입니다.");


    private final String message;

}
