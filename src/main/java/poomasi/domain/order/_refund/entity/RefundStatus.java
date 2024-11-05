package poomasi.domain.order._refund.entity;

public enum RefundStatus {
    REQUESTED("환불 요청됨"),
    PROCESSING("환불 처리 중"),
    COMPLETED("환불 완료됨"),
    REJECTED("환불 거절됨");

    private final String description;

    RefundStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

/*
* REQUESTED,       // 반품 요청됨
    APPROVED,        // 반품 승인됨
    REJECTED,        // 반품 거부됨
    RETURNED,        // 상품이 반품됨
    REFUNDED,        // 환불 완료됨
    CANCELLED,       // 반품 요청이 취소됨
    IN_TRANSIT      // 반품이 배송 중
    ;
* */
