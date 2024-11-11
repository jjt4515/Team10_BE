package poomasi.domain.order.entity._product;


import jakarta.persistence.*;
import jdk.jfr.Description;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import poomasi.domain.order._aftersales.entity._product.ProductAfterSales;
import poomasi.domain.order.entity._abstract.AbstractOrder;

import java.util.List;

@Entity
@Table(name = "product_order")
@Getter
@NoArgsConstructor
@SQLDelete(sql = "UPDATE product_order SET deleted_at=current_timestamp WHERE id = ?")
public class ProductOrder extends AbstractOrder {

    @Column(name = "ordered_products_id")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderedProduct> orderedProducts;

    @OneToOne
    @JoinColumn(name = "product_order_details_id") // 외래 키 지정
    @Description("상품 배송지, 요청 사항")
    private ProductOrderDetails productOrderDetails;


    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "product_after_sales_id")
    @Description("결제 완료 후 취소/교환/환불 관리 객체")
    private ProductAfterSales productAfterSales;

    public ProductOrder(ProductOrderDetails productOrderDetails) {
        this.productOrderDetails = productOrderDetails;
    }

    public void addOrderedProduct(OrderedProduct orderedProduct) {
        this.orderedProducts.add(orderedProduct);
    }

}
