package poomasi.domain.member._profile.dto.request;

import poomasi.domain.member._profile.entity.MemberProfile;

public record MemberProfileRequest(String name) {
    public static MemberProfile toEntity(MemberProfileRequest memberProfileRequest){
        return new MemberProfile(
                memberProfileRequest.name
        );
    }
}
