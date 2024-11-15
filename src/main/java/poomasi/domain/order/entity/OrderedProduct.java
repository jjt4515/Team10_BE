package poomasi.domain.order.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import jakarta.persistence.*;
import jdk.jfr.Description;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import poomasi.domain.aftersales.entity.ProductAfterSales;
import poomasi.domain.member.entity.Member;
import poomasi.domain.product.entity.Product;
import poomasi.domain.review.entity.Review;
import poomasi.payment.entity.Payment;

import java.math.BigDecimal;

import static poomasi.domain.order.entity.OrderedProductStatus.PENDING_SELLER_APPROVAL;

@Entity
@Table(name = "ordered_products")
@Getter
@NoArgsConstructor
public class OrderedProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ordered_product_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = true, name = "product_after_sales_id")
    private ProductAfterSales productAfterSales;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_order_id")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "product_description", nullable = true)
    private String productDescription;

    @Column(name = "product_name", length = 255)
    private String productName;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "grow_env")
    private String growEnv;

    @Description("구매 당시 1개당 가격")
    private BigDecimal price;

    @Description("구매 수량")
    @Column(name = "count")
    private Integer count;

    @Description("택배 회사")
    @Column(name = "delivery_service", nullable = true)
    private String deliveryService;

    @Description("송장 번호")
    @Column(name = "invoice_number", nullable = true)
    private String invoiceNumber;

    @Enumerated(EnumType.STRING)
    private OrderedProductStatus orderedProductStatus = PENDING_SELLER_APPROVAL;

    @Description("TODO : product의 delivery fee를 참조해야 한다.")
    private BigDecimal deliveryFee;

    @Description("flag가 설정되어 있으면 배송비 환불하지 않아도 된다")
    private boolean isCanceled = false;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Setter
    private Review review;


    @Builder
    public OrderedProduct(Product product, Order order, String productDescription,
                          String productName, BigDecimal price, Integer count, BigDecimal deliveryFee, String imageUrl, String growEnv) {
        this.product = product;
        this.order = order;
        this.productDescription = productDescription;
        this.productName = productName;
        this.price = price;
        this.count = count;
        this.deliveryFee = deliveryFee;
        this.imageUrl = imageUrl;
        this.growEnv = growEnv;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public void setOrderedProductStatus(OrderedProductStatus orderedProductStatus) {
        this.orderedProductStatus = orderedProductStatus;
    }

    public void addProductAfterSalesDetail(ProductAfterSales productAfterSales) {
        this.productAfterSales = productAfterSales;
        productAfterSales.setOrderedProduct(this);
    }

    public Long getOrderId() {
        return this.order.getId();
    }

    public Member getStoreOwner(){
        return this.product.getStore().getOwner();
    }

    public String getStoreName(){
        return this.product.getStore().getName();
    }

    public String getStoreAddress() {
        return this.product.getStore().getAddress();
    }

    public Long getProductId(){
        return this.product.getId();
    }

    public Payment getPayment(){
        return this.order.getPayment();
    }

    public BigDecimal calculateCancelAmount(){
        BigDecimal count = new BigDecimal(this.count);
        return this.price.multiply(count).add(deliveryFee);
    }

    public BigDecimal calculateRefundAmount(){
        BigDecimal count = new BigDecimal(this.count);
        return this.price.multiply(count);
    }
}

