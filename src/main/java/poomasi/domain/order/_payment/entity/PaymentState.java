package poomasi.domain.order._payment.entity;


import jdk.jfr.Description;

@Description("임시로 남겨둠 .. . .")
public enum PaymentState {
    PENDING,        // 결제 대기 중
    COMPLETED,      // 결제 완료
    FAILED,         // 결제 실패
    CANCELLED,      // 결제 취소됨
    REFUNDED,       // 환불 완료
    DECLINED;       // 결제 거부됨

    @Override
    public String toString() {
        // 사용자 친화적인 문자열로 반환할 수 있도록 오버라이딩
        switch (this) {
            case PENDING:
                return "Payment Pending";
            case COMPLETED:
                return "Payment Completed";
            case FAILED:
                return "Payment Failed";
            case CANCELLED:
                return "Payment Cancelled";
            case REFUNDED:
                return "Payment Refunded";
            case DECLINED:
                return "Payment Declined";
            default:
                return super.toString();
        }
    }
}
