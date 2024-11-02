package poomasi.domain.order._payment.entity;


import jakarta.persistence.*;
import jdk.jfr.Description;
import lombok.Getter;
import poomasi.domain.order.entity.Order;

import java.math.BigDecimal;

@Entity
@Getter
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Description("상품 총 가격")
    private BigDecimal totalPrice;

    @Description("할인 가격")
    private BigDecimal discountPrice;
    
    @Description("최종 가격")
    private BigDecimal finalPrice;
        
    @Description("결제 방식")
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @OneToOne
    private Order order;

    @Description("포트원에서 결제 식별을 위한 merchant_uid")
    @Column(name = "merchant_uid" , updatable = false)
    private String merchantUid;

}
