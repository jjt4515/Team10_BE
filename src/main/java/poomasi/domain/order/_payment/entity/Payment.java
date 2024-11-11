package poomasi.domain.order._payment.entity;

import jakarta.persistence.*;
import jdk.jfr.Description;
import lombok.Getter;
import poomasi.domain.order.entity._farm.FarmOrder;
import poomasi.domain.order.entity._product.ProductOrder;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Getter
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Description("포트원 결제 금액")
    private BigDecimal totalPrice;

    @Description("할인 가격")
    private BigDecimal discountPrice;

    @Description("사용 포인트")
    private BigDecimal usedPoint;

    @Description("최종 가격")
    private BigDecimal finalPrice;
        
    @Description("결제 방식")
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @OneToOne(mappedBy = "payment")
    private ProductOrder productOrder;

/*    @OneToOne(mappedBy = "payment")
    private FarmOrder farmOrder;*/

}
