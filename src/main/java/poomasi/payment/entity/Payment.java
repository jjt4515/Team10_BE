package poomasi.payment.entity;

import jakarta.persistence.*;
import jdk.jfr.Description;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.Setter;
import poomasi.domain.order.entity.Order;
import poomasi.domain.reservation.entity.Reservation;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import poomasi.domain.reservation.entity.Reservation;

@Entity
@Getter
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "imp_uid")
    @Setter
    @Description("아임포트 결제 imp_uid")
    private String impUid;

    @Getter
    @Setter
    @OneToOne(mappedBy = "payment")
    private Order order;

    @Setter
    @Getter
    @OneToOne(mappedBy = "payment")
    private Reservation reservation;

    @Description("포트원 결제 금액")
    private BigDecimal totalAmount;

    @Description("결제 방식")
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod = PaymentMethod.TOSS_PAYMENTS;

    @Description("checksum")
    private BigDecimal checkSum;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus = PaymentStatus.PAYMENT_PENDING;

    @Enumerated(EnumType.STRING)
    private ItemType itemType;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Payment(){
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    @Builder
    public Payment(String impUid, Order order,
                   Reservation reservation, BigDecimal totalAmount, BigDecimal checkSum, ItemType itemType) {
        this.impUid = impUid;
        this.order = order;
        this.reservation = reservation;
        this.totalAmount = totalAmount;
        this.checkSum = checkSum;
        this.itemType = itemType;
    }


    public void setCheckSum(BigDecimal checksum) {
        this.checkSum = checksum;
    }

    public void subtractCheckSum(BigDecimal checksum) {
        this.checkSum = this.checkSum.subtract(checksum);
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    @Description("체크섬보다 크면 true 후 체크섬 빼기, 아니면 false")
    public boolean isCheckSumValid(BigDecimal amount){
        if(checkSum.compareTo(amount) >= 0){
            checkSum = checkSum.subtract(amount);
            return true;
        }
        return false;
    }


}
