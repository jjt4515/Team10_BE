package poomasi.domain.order.entity;

import jakarta.persistence.*;
import jdk.jfr.Description;
import jdk.jfr.Timestamp;
import lombok.Getter;
import org.hibernate.annotations.UpdateTimestamp;
import poomasi.domain.member.entity.Member;

import java.time.LocalDateTime;
import java.util.Date;

@MappedSuperclass
@Getter
public abstract class AbstractOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "member_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @Column(name = "merchant_uid")
    @Description("상품당 결제 id(아임포트 id)")
    private String merchantUid = "p" + new Date().getTime();

    @Column(name = "created_at")
    @Timestamp
    private LocalDateTime createdAt = LocalDateTime.now();

}
