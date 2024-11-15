package poomasi.domain.aftersales.entity._product;

import jakarta.persistence.*;
import jdk.jfr.Description;
import jdk.jfr.Timestamp;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import poomasi.domain.order.entity.OrderedProduct;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Description("상품 판매 후 교환/환불/추소 history")
@Entity
@Getter
@Table(name="product_after_sales_detail")
@NoArgsConstructor
public class ProductAfterSalesDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updateAt = LocalDateTime.now();

    @Column(name = "deleted_at")
    @Timestamp
    private LocalDateTime deletedAt;

    @OneToOne
    private OrderedProduct orderedProduct;

    @Enumerated(EnumType.STRING)
    private ProductAfterSalesStatus productAfterSalesStatus;

    @Builder
    public ProductAfterSalesDetail(OrderedProduct orderedProduct, ProductAfterSalesStatus productAfterSalesStatus) {
        this.orderedProduct = orderedProduct;
        this.productAfterSalesStatus = productAfterSalesStatus;
    }

    public void setOrderedProduct(OrderedProduct orderedProduct) {
        this.orderedProduct = orderedProduct;
    }

    public void setProductAfterSalesStatus(ProductAfterSalesStatus productAfterSalesStatus){
        this.productAfterSalesStatus = productAfterSalesStatus;
    }



}

