package poomasi.domain.order.entity._abstract;

import jakarta.persistence.*;
import jdk.jfr.Description;
import jdk.jfr.Timestamp;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.UpdateTimestamp;
import poomasi.domain.member.entity.Member;
import poomasi.domain.order._payment.entity.Payment;
import poomasi.domain.order.entity.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

@MappedSuperclass
@Getter
public abstract class AbstractOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "member_id")
    @ManyToOne(fetch = FetchType.LAZY)
    @Description("주문 한 사람을 참조한다.")
    private Member member;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Payment payment;

    @Column(name = "merchant_uid")
    @Description("서버 내부 주문 id(아임포트 id)")
    private String merchantUid = "p" + new Date().getTime();

    @Column(name = "imp_uid")
    @Description("아임포트 결제 imp_uid")
    private String impUid;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updateAt = LocalDateTime.now();

    @Column(name = "deleted_at")
    @Timestamp
    private LocalDateTime deletedAt;

    @Column(name = "total_amount")
    @Description("총 결제 금액")
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus = OrderStatus.PENDING;

    @Description("checksum")
    private BigDecimal checksum;

    public void setCheckSum(BigDecimal checksum) {
        this.checksum = checksum;
    }

    public void reduceChecksum(BigDecimal amount) {
        checksum = checksum.subtract(amount);
    }
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public void setImpUid(String impUid) {
        this.impUid = impUid;
    }

    public void setMerchantUid(String merchantUid) {
        this.merchantUid = merchantUid;
    }

}

