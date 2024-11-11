package poomasi.domain.order._aftersales.entity._abstract;

import jakarta.persistence.*;
import jdk.jfr.Timestamp;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@MappedSuperclass
public abstract class AbstractAfterSales {

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

}

/*
* package poomasi.domain.order._aftersales.entity;

import jakarta.persistence.*;
import jdk.jfr.Description;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import poomasi.domain.order._payment.entity.Payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name="refund_history")
public class Refund {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Description("삭제 시간")
    private LocalDateTime deletedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    private Payment payment;


    private BigDecimal refundAmount;

   @OneToOne(fetch = FetchType.LAZY)
    private OrderedProduct orderProductDetails;

private String refundReason;

}

        *
* */

