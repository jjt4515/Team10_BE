package poomasi.domain.member._profile.dto.response;

import poomasi.domain.member._profile.entity.CustomerProfile;
import poomasi.domain.member._profile.entity.FarmerProfile;
import poomasi.domain.member._profile.entity.MemberProfile;

public interface MemberProfileResponse {

    static MemberProfileResponse fromEntity(MemberProfile profile) {
        if (profile instanceof FarmerProfile farmerProfile) {
            return FarmerProfileResponse.fromEntity(farmerProfile);
        } else if (profile instanceof CustomerProfile customerProfile) {
            return CustomerProfileResponse.fromEntity(customerProfile);
        } else {
            return CommonProfileResponse.fromEntity(profile);
        }
    }
}