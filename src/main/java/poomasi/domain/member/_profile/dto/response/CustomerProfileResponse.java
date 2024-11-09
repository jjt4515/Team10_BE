package poomasi.domain.member._profile.dto.response;

import poomasi.domain.image.entity.Image;
import poomasi.domain.member._profile.entity.CustomerProfile;

import java.time.LocalDateTime;

public record CustomerProfileResponse(
        String phoneNumber,
        String address,
        String addressDetail,
        boolean isBanned,
        LocalDateTime createdAt,
        Image profileImage
) {
    public static CustomerProfileResponse fromEntity(CustomerProfile profile) {
        return new CustomerProfileResponse(
                profile.getPhoneNumber(),
                profile.getAddress(),
                profile.getAddressDetail(),
                profile.isBanned(),
                profile.getCreatedAt(),
                profile.getProfileImage()
        );
    }
}