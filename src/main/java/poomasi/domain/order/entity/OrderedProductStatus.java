package poomasi.domain.order.entity;

public enum OrderedProductStatus {
    
    PENDING_SELLER_APPROVAL, // 판매자 수락 전 (주문 완료 후 대기 상태)
    SHIPMENT_STARTED,        // 배송 시작 (판매자 수락을 하면 바뀌는 상태)
    IN_TRANSIT,              // 배송 중
    DELIVERED,                // 배송 완료

    //환불
    REFUND_REQUESTED,           // 환불 요청됨
    REFUND_APPROVED,            // 환불 승인됨

    //주문 취소
    CANCEL_PENDING           // 주문 취소 상태
    ;
}