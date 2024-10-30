package poomasi.domain.member.dto;

import poomasi.domain.member.entity.MemberProfile;

import java.time.LocalDateTime;

public record MemberProfileResponse(
        String name,
        String phoneNumber,
        String address,
        String addressDetail,
        Long coordinateX,
        Long coordinateY,
        boolean isBanned,
        LocalDateTime createdAt
) {
    public static MemberProfileResponse fromEntity(MemberProfile profile) {
        return new MemberProfileResponse(
                profile.getName(),
                profile.getPhoneNumber(),
                profile.getAddress(),
                profile.getAddressDetail(),
                profile.getCoordinateX(),
                profile.getCoordinateY(),
                profile.isBanned(),
                profile.getCreatedAt()
        );
    }
}