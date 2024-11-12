package poomasi.domain.order._refund.entity;


import jakarta.persistence.*;
import poomasi.domain.order.entity.OrderProductDetails;

@Entity
@Table(name="refund")
public class Refund {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

/*    @OneToOne(fetch = FetchType.LAZY)
    private OrderProductDetails orderProductDetails;*/

    private String refundReason;

}
