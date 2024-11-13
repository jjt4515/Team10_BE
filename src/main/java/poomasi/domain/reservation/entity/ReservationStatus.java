package poomasi.domain.reservation.entity;

public enum ReservationStatus {
    PENDING, // 대기
    ACCEPTED, // 결제 완료
    REJECTED, // 거절
    CANCELED, // 취소
    DONE // 완료
    ;
}
