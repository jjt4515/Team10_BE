package poomasi.domain.order.entity._product;

public enum ShippingStatus {
    ORDERED,         // 주문 완료 (배송 전 시작 상태) -> 배송 보내기 전 단계. 판매자 확인 후 취소 가능
    SHIPMENT_STARTED, // 배송 시작
    IN_TRANSIT,      // 배송 중
    DELIVERED        // 배송 완료
}
