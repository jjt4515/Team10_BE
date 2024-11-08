package poomasi.domain.order.entity._product;


import jakarta.persistence.*;
import jdk.jfr.Description;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import poomasi.domain.order._aftersales.entity._product.ProductAfterSalesDetail;
import poomasi.domain.product.entity.Product;

import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "ordered_products")
@Getter
@NoArgsConstructor
public class OrderedProduct implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ordered_product_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = true, name = "product_after_sales_detail_id")
    private ProductAfterSalesDetail productAfterSalesDetail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_order_id")
    private ProductOrder productOrder;


    //FIXME : store Id를 참조해야 한다.
    //나중에 store Id로 변경해야 한다
    private Long storeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;



    @Column(name = "product_description", nullable = true)
    private String productDescription;

    @Column(name = "product_name", length = 255)
    private String productName;

    @Description("구매 당시 1개당 가격")
    private BigDecimal price;

    @Column(name="count")
    private Integer count;

    @Description("송장 번호")
    @Column(name = "invoice_number", nullable = true)
    private String invoiceNumber;

    private ShippingStatus shippingStatus = ShippingStatus.ORDERED;
    
    // 웹훅 받아서 조회해야 함.
    // findByInvoiceNumber 후 
    // web hook controller 만들어서
    // 배송 상태 적절히 변경해야 함

    @Builder
    public OrderedProduct(Product product, ProductOrder productOrder, String productDescription, String productName, BigDecimal price, Integer count) {
        this.product = product;
        this.productOrder = productOrder;
        this.productDescription = productDescription;
        this.productName = productName;
        this.price = price;
        this.count = count;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public void setShippingStatus(ShippingStatus shippingStatus) {
        this.shippingStatus = shippingStatus;
    }

    public Long getOrderId(){
        return this.productOrder.getId();
    }
}

