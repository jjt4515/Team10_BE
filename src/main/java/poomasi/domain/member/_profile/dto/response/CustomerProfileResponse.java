package poomasi.domain.member._profile.dto.response;

import poomasi.domain.image.entity.Image;
import poomasi.domain.member._profile.entity.MemberProfile;

import java.time.LocalDateTime;

public record CustomerProfileResponse(
        String phoneNumber,
        String address,
        String addressDetail,
        Long coordinateX,
        Long coordinateY,
        boolean isBanned,
        LocalDateTime createdAt,
        Image profileImage){


    public static CustomerProfileResponse fromEntity(MemberProfile profile) {
        return new CustomerProfileResponse(
                profile.getPhoneNumber(),
                profile.getAddress(),
                profile.getAddressDetail(),
                profile.getCoordinateX(),
                profile.getCoordinateY(),
                profile.isBanned(),
                profile.getCreatedAt(),
                profile.getProfileImage()
        );
    }
}