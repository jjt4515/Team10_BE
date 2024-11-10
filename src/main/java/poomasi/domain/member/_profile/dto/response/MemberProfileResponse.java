package poomasi.domain.member._profile.dto.response;

import poomasi.domain.image.entity.Image;
import poomasi.domain.member._profile.entity.MemberProfile;

import java.time.LocalDateTime;
import java.util.List;

public record MemberProfileResponse(
        String phoneNumber,
        String address,
        String addressDetail,
        Long coordinateX,
        Long coordinateY,
        List<String> businessRegistrationNumbers,
        boolean isBanned,
        LocalDateTime createdAt,
        Image profileImage){


    public static MemberProfileResponse fromEntity(MemberProfile profile) {
        return new MemberProfileResponse(
                profile.getPhoneNumber(),
                profile.getAddress(),
                profile.getAddressDetail(),
                profile.getCoordinateX(),
                profile.getCoordinateY(),
                profile.getBusinessRegistrationNumbers(),
                profile.isBanned(),
                profile.getCreatedAt(),
                profile.getProfileImage()
        );
    }
}