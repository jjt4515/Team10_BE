package poomasi.domain.member.dto.response;

import poomasi.domain.image.entity.Image;
import poomasi.domain.member.entity.Member;

public record MemberSummaryResponse(String name, Image profileImage) {
    public static MemberSummaryResponse fromEntity(Member member) {
        return new MemberSummaryResponse(
                member.getMemberProfile().getName(),
                member.getMemberProfile().getProfileImage()
        );
    }
}
