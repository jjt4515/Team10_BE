package poomasi.domain.order.entity;

public enum OrderStatus {
    PENDING,      // 결제 대기 중
    AWAITING_SELLER_CONFIRMATION, // 판매자 확인 대기 중
    READY_FOR_SHIPMENT,   // 배송 대기 중
    IN_TRANSIT,           // 배송 중
    DELIVERED,            // 배송 완료
    ORDER_COMPLETE        // 주문 완료
    ;
}