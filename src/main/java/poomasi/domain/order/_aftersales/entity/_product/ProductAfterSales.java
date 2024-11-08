package poomasi.domain.order._aftersales.entity._product;

import jakarta.persistence.*;
import poomasi.domain.order._aftersales.entity._abstract.AbstractAfterSales;
import poomasi.domain.order.entity._product.ProductOrder;

import java.util.List;

@Entity
@Table(name="product_after_sales")
public class ProductAfterSales extends AbstractAfterSales {

    @OneToOne
    @JoinColumn(name = "product_order_id")
    private ProductOrder productOrder;

    @OneToMany
    private List<ProductAfterSalesDetail> productAfterSalesDetail;

}

