package poomasi.domain.order._payment.dto.response;

public record PaymentPreRegisterResponse(String merchantUid) {

    public static PaymentPreRegisterResponse from(String merchantUid){
        return new PaymentPreRegisterResponse(merchantUid);
    }
}
