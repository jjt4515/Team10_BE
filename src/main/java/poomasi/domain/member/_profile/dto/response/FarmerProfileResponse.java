package poomasi.domain.member._profile.dto.response;

import poomasi.domain.image.entity.Image;
import poomasi.domain.member._profile.entity.MemberProfile;

import java.time.LocalDateTime;
import java.util.List;

public record FarmerProfileResponse(
        String phoneNumber,
        boolean isBanned,
        LocalDateTime createdAt,
        Image profileImage,
        List<String> businessRegistrationNumbers
){
    public static FarmerProfileResponse fromEntity(MemberProfile profile) {
        return new FarmerProfileResponse(
                profile.getPhoneNumber(),
                profile.isBanned(),
                profile.getCreatedAt(),
                profile.getProfileImage(),
                profile.getBusinessRegistrationNumbers()
        );
    }
}