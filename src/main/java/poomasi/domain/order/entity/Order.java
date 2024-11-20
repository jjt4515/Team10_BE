package poomasi.domain.order.entity;


import jakarta.persistence.*;
import jdk.jfr.Description;
import jdk.jfr.Timestamp;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.UpdateTimestamp;
import poomasi.domain.member.entity.Member;
import poomasi.payment.entity.Payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static poomasi.payment.entity.PaymentStatus.PAYMENT_COMPLETE;
import static poomasi.payment.entity.PaymentStatus.PAYMENT_DECLINED;

@Entity
@Table(name = "product_order")
@Getter
@SQLDelete(sql = "UPDATE product_order SET deleted_at = current_timestamp WHERE id = ?")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "member_id")
    @ManyToOne(fetch = FetchType.LAZY)
    @Description("주문 한 사람을 참조한다.")
    private Member member;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Payment payment;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updateAt = LocalDateTime.now();

    @Comment("예약 취소 일자")
    private LocalDateTime canceledAt;

    @Column(name = "deleted_at")
    @Timestamp
    private LocalDateTime deletedAt;

    @Column(name = "total_amount")
    @Description("총 결제 금액 : 모든 품목들의 배송비 + 가격")
    private BigDecimal totalAmount;

    @Column(name = "merchant_uid")
    @Description("서버 내부 주문 id(아임포트 id)")
    private String merchantUid;

    @Column(name = "ordered_products_id")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderedProduct> orderedProducts = new ArrayList<>();

    @Column(name = "address")
    @Description("도착 주소")
    private String address;

    @Column(name = "address_detail")
    @Description("도착 상세 주소")
    private String addressDetail;

    @Description("배송 요청 사항")
    @Column(name = "delivery_request", length = 255)
    private String deliveryRequest;

    public Order(){}

    @Builder
    public Order(Member member,
                 Payment payment,
                 BigDecimal totalAmount,
                 String address,
                 String addressDetail,
                 String deliveryRequest ) {
        this.member = member;
        this.payment = payment;
        this.totalAmount = totalAmount;
        this.address = address;
        this.addressDetail = addressDetail;
        this.deliveryRequest = deliveryRequest;
    }

    public String getPaymentMethod(){
        return this.payment.getPaymentMethod().toString();
    }

    public void addTotalAmount(BigDecimal amount){
        this.totalAmount = this.totalAmount.add(amount);
    }

    public void addOrderedProduct(OrderedProduct orderedProduct){
        this.orderedProducts.add(orderedProduct);
        orderedProduct.setOrder(this);
    }

    public void setMerchantUid(String merchantUid){
        this.merchantUid = merchantUid;
    }

    public void setPayment(Payment payment){
        this.payment=payment;
        payment.setOrder(this);
    }

    public void setPaymentComplete(String impUid){
        this.payment.setPaymentStatus(PAYMENT_COMPLETE);
        this.payment.setImpUid(impUid);
    }

    public void cancel(){
        this.payment.setPaymentStatus(PAYMENT_DECLINED);
        this.canceledAt = LocalDateTime.now();
    }


    public void setImpUid(String impUid){
        this.payment.setImpUid(impUid);
    }

}
