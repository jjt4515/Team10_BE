package poomasi.domain.order.entity;

public enum OrderStatus {
    PENDING,      // 결제 대기 중
    AWAITING_SELLER_CONFIRMATION, // 판매자 확인 대기 중
    SELLER_CONFIRMED             // 판매자 확인 완료
    ;
}