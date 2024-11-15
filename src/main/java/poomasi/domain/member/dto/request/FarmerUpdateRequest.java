package poomasi.domain.member.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record FarmerUpdateRequest(
        String name,

        @Email(message = "잘못된 이메일 형식입니다.")
        String email,

        @Size(min = 8, max = 20, message = "비밀번호는 8자 ~ 20자 사이로 설정해주세요.")
        String password,

        @Pattern(
                regexp = "^[0-9]{10,15}$",
                message = "전화번호 숫자는 10 ~ 15개여야 합니다."
        )
        String phoneNumber,
        String storeName,
        String storeAddress) {
}
