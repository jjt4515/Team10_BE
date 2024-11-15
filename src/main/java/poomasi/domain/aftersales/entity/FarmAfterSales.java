package poomasi.domain.aftersales.entity;


import jakarta.persistence.*;
import jdk.jfr.Description;
import jdk.jfr.Timestamp;
import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.UpdateTimestamp;
import poomasi.domain.reservation.entity.Reservation;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table
@Data
public class FarmAfterSales {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Description("금액")
    private BigDecimal afterSalesAmount;

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
    private Reservation reservation;

    public FarmAfterSales(){

    }

    @Builder
    public FarmAfterSales(BigDecimal afterSalesAmount, Reservation reservation) {
        this.afterSalesAmount = afterSalesAmount;
        this.reservation = reservation;
    }
}
