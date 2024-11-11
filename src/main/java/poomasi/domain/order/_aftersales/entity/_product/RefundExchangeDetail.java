package poomasi.domain.order._aftersales.entity._product;


import jakarta.persistence.*;
import jdk.jfr.Description;

@Entity
@Table(name= "refund_exchange_detail")
public class RefundExchangeDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "refundExchangeDetail") // 주인이 아닌 쪽에 mappedBy 설정
    private ProductAfterSalesDetail productAfterSalesDetail;

    @Description("반품 회수지. 기본 값은 보낸 주소")
    private String pickupLocation;

    @Description("반송지. 기본 값은 받은 주소")
    private String returnAddress;

    @Description("반품/교환 시 운송장 번호")
    private String invoiceNumber;
    
    @Description("반품/교환 시 요청 사항")
    private String request;
}
