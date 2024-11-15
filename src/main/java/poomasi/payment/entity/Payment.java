package poomasi.payment.entity;

import jakarta.persistence.*;
import jdk.jfr.Description;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import poomasi.domain.order.entity.PaymentStatus;
import poomasi.domain.order.entity.Order;

import java.math.BigDecimal;
import poomasi.domain.reservation.entity.Reservation;

@Entity
@Getter
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "imp_uid")
    @Description("아임포트 결제 imp_uid")
    private String impUid;

    @OneToOne(mappedBy = "payment")
    private Order order;

    @OneToOne(mappedBy = "payment")
    private Reservation reservation;

    @Description("포트원 결제 금액")
    private BigDecimal totalAmount;

    @Description("결제 방식")
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Description("checksum")
    private BigDecimal checkSum;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus = PaymentStatus.PAYMENT_PENDING;

    @Enumerated(EnumType.STRING)
    private ItemType itemType;

    public Payment(){
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
}
