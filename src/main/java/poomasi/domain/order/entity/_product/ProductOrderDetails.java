package poomasi.domain.order.entity._product;

import jakarta.persistence.*;
import jdk.jfr.Description;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name="product_order_details")
@Getter
@NoArgsConstructor
public class ProductOrderDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "productOrderDetails", cascade = CascadeType.ALL) // 필드명으로 지정
    private ProductOrder productOrder;

    @Column(name = "address")
    private String address;

    @Column(name = "address_detail")
    private String addressDetail;

    @Description("배송 요청 사항")
    @Column(name = "delivery_request", length = 255)
    private String deliveryRequest;

    public ProductOrderDetails(String address, String addressDetail, String deliveryRequest) {
        this.address = address;
        this.addressDetail = addressDetail;
        this.deliveryRequest = deliveryRequest;
    }


}
