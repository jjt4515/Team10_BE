package poomasi.domain.member._profile.dto.response;

import poomasi.domain.image.entity.Image;
import poomasi.domain.member._profile.entity.FarmerProfile;

import java.time.LocalDateTime;
import java.util.List;

public record FarmerProfileResponse(
        String phoneNumber,
        boolean isBanned,
        LocalDateTime createdAt,
        Image profileImage,
        String storeName,
        String farmName,
        List<String> businessRegistrationNumbers,
        String storeAddress,
        String storeAddressDetail,
        String farmAddress,
        String farmAddressDetail
) {
    public static FarmerProfileResponse fromEntity(FarmerProfile profile) {
        return new FarmerProfileResponse(
                profile.getPhoneNumber(),
                profile.isBanned(),
                profile.getCreatedAt(),
                profile.getProfileImage(),
                profile.getStoreName(),
                profile.getFarmName(),
                profile.getBusinessRegistrationNumbers(),
                profile.getStoreAddress(),
                profile.getStoreAddressDetail(),
                profile.getFarmAddress(),
                profile.getFarmAddressDetail()
        );
    }
}