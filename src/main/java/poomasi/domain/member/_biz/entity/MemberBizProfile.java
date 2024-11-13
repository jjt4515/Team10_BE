package poomasi.domain.member._biz.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.Comment;


@Entity
@Getter
@Comment("사업자 회원 프로필")
@Table(name = "member_biz_profile")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberBizProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Comment("사업자 번호")
    @Column(nullable = false)
    private String bizNumber;

    @Comment("사업자명")
    @Column(nullable = false)
    private String bizName;

    @Comment("사업자 등록증 이미지")
    @Column(nullable = false)
    private String bizRegImage;

    @Setter
    @Comment("관리자 승인 필요 여부")
    @Column(nullable = false)
    private boolean needsAdminApproval;

    @Comment("회원 ID")
    @Column(nullable = false)
    private Long memberId;

    @Builder
    public MemberBizProfile(String bizNumber, String bizName, String bizRegImage, Long memberId, boolean needsAdminApproval) {
        this.bizNumber = bizNumber;
        this.bizName = bizName;
        this.bizRegImage = bizRegImage;
        this.memberId = memberId;
        this.needsAdminApproval = needsAdminApproval;
    }

}
