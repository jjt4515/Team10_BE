package poomasi.domain.member._biz.dto.request;

import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.Comment;
import poomasi.domain.member._biz.entity.MemberBizProfile;

public record BizProfileCreateRequest(
        @NotNull
        @Comment("사업자 번호")
        String number,
        @NotNull
        @Comment("사업자 등록증 이미지")
        String imageUrl

) {
    public MemberBizProfile toEntity(Long id, boolean needsAdminApproval) {
        return MemberBizProfile.builder()
                .bizNumber(number)
                .bizRegImage(imageUrl)
                .memberId(id)
                .needsAdminApproval(needsAdminApproval)
                .build();
    }
}
