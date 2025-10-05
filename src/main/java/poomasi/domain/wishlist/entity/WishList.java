package poomasi.domain.wishlist.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CurrentTimestamp;
import org.hibernate.annotations.SQLDelete;
import poomasi.domain.member.entity.Member;
import poomasi.global.common.ServiceType;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@SQLDelete(sql = "UPDATE product SET deleted_at = current_timestamp WHERE id = ?")
public class WishList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Comment("회원")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Comment("상품 혹은 농장 아이디")
    private Long objectId;

    @Comment("상품 혹은 농장 구분")
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ServiceType type;

    @Comment("등록일시")
    @CurrentTimestamp
    private LocalDateTime createdAt;

    @Comment("삭제일시")
    private LocalDateTime deletedAt;

    @Builder
    public WishList(Member member, Long objectId, ServiceType type) {
        this.member = member;
        this.objectId = objectId;
        this.type = type;
    }

}
