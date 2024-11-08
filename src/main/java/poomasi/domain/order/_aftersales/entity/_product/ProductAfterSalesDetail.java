    package poomasi.domain.order._aftersales.entity._product;

    import jakarta.persistence.*;
    import jdk.jfr.Description;
    import poomasi.domain.order.entity._product.OrderedProduct;

    import java.math.BigDecimal;

    @Description("상품 판매 후 교환/환불/추소 history")
    @Entity
    @Table(name="product_after_sales_detail")
    public class ProductAfterSalesDetail {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne
        private ProductAfterSales productAfterSales;

        @OneToOne
        private OrderedProduct orderedProduct;


        @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
        @JoinColumn(name = "refund_exchange_detail_id", nullable = true) // 외래 키 설정
        private RefundExchangeDetail refundExchangeDetail;

        @Description("ordered products의 환불/교환/취소 금액")
        private BigDecimal amount;

        @Description("환불/교환/취소 사유")
        private String reason;

        @Description("환불 받을 계좌번호")
        private String refundAccount;

        @Enumerated(EnumType.STRING)
        private ProductAfterSalesType productAfterSalesType;


    }
