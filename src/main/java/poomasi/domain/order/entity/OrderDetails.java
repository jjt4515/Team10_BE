package poomasi.domain.order.entity;

import jakarta.persistence.*;
import jdk.jfr.Description;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name="order_details")
@Getter
@NoArgsConstructor
public class OrderDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "orderDetails")
    private Order order;

    @Column(name = "address")
    private String address;

    @Column(name = "address_detail")
    private String addressDetail;

    @Description("배송 요청 사항")
    @Column(name = "delivery_request", length = 255)
    private String deliveryRequest;

    public OrderDetails(String address, String addressDetail, String deliveryRequest) {
        this.address = address;
        this.addressDetail = addressDetail;
        this.deliveryRequest = deliveryRequest;
    }


}
