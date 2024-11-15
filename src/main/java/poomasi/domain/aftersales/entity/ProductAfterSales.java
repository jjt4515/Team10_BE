package poomasi.domain.aftersales.entity;

import jakarta.persistence.*;
import jdk.jfr.Description;
import jdk.jfr.Timestamp;
import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.UpdateTimestamp;
import poomasi.domain.order.entity.OrderedProduct;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name="product_after_sales")
public class ProductAfterSales {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Description("금액")
    private BigDecimal afterSalesAmount;

    @Description("타입")
    private AfterSalesType afterSalesType;

    @Column(name = "created_at")
    @UpdateTimestamp
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updateAt = LocalDateTime.now();

    @Column(name = "deleted_at")
    @Timestamp
    private LocalDateTime deletedAt;

    @OneToOne
    private OrderedProduct orderedProduct;

    public ProductAfterSales(){
    }


}
