package poomasi.domain.member._profile.dto.response;

import lombok.AllArgsConstructor;
import poomasi.domain.image.entity.Image;
import poomasi.domain.member._profile.entity.MemberProfile;

import java.time.LocalDateTime;

@AllArgsConstructor
public class CommonProfileResponse implements MemberProfileResponse {
    String phoneNumber;
    boolean isBanned;
    LocalDateTime createdAt;
    Image profileImage;

    public static CommonProfileResponse fromEntity(MemberProfile profile) {
        return new CommonProfileResponse(
                profile.getPhoneNumber(),
                profile.isBanned(),
                profile.getCreatedAt(),
                profile.getProfileImage()
        );
    }

}
