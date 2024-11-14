package poomasi.domain.order.entity;


import jakarta.persistence.*;
import jdk.jfr.Description;
import jdk.jfr.Timestamp;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.UpdateTimestamp;
import poomasi.domain.member.entity.Member;
import poomasi.payment.entity.Payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "product_order")
@Getter
@Builder
@SQLDelete(sql = "UPDATE product_order SET deleted_at = current_timestamp WHERE id = ?")
public class ProductOrder {

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

    @Column(name = "deleted_at")
    @Timestamp
    private LocalDateTime deletedAt;

    @Column(name = "total_amount")
    @Description("총 결제 금액")
    private BigDecimal totalAmount;

    @Column(name = "merchant_uid")
    @Description("서버 내부 주문 id(아임포트 id)")
    private String merchantUid = "p" + new Date().getTime();

    @Column(name = "ordered_products_id")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderedProduct> orderedProducts;

    @OneToOne
    @JoinColumn(name = "product_order_details_id") // 외래 키 지정
    @Description("상품 배송지, 요청 사항")
    private ProductOrderDetails productOrderDetails;

    public ProductOrder(){}

    @Builder
    public ProductOrder(Long id, Member member, Payment payment, LocalDateTime createdAt, LocalDateTime updateAt,
                 LocalDateTime deletedAt, BigDecimal totalAmount, String merchantUid,
                 List<OrderedProduct> orderedProducts, ProductOrderDetails productOrderDetails) {
        this.id = id;
        this.member = member;
        this.payment = payment;
        this.createdAt = createdAt;
        this.updateAt = updateAt;
        this.deletedAt = deletedAt;
        this.totalAmount = totalAmount;
        this.merchantUid = merchantUid;
        this.orderedProducts = orderedProducts;
        this.productOrderDetails = productOrderDetails;
    }


    public void addOrderedProduct(OrderedProduct orderedProduct) {
        this.orderedProducts.add(orderedProduct);
    }

    public void setMerchantUid(String merchantUid) {
        this.merchantUid = merchantUid;
    }

    public String getImpUid(){
        return this.getPayment().getImpUid();
    }

    public PaymentStatus getPaymentStatus(){
        return this.getPayment().getPaymentStatus();
    }

    public void setPaymentStatus(PaymentStatus paymentStatus){
        this.getPayment().setPaymentStatus(paymentStatus);
    }

    public void setProductOrderDetails(ProductOrderDetails productOrderDetails){
        this.productOrderDetails = productOrderDetails;
        productOrderDetails.setProductOrder(this);
    }

    public BigDecimal getChecksum(){
        return this.payment.getCheckSum();
    }

    /*public void subtractChecksum(BigDecimal checkSum){
        this.payment.getCheckSum().

    }*/


}
