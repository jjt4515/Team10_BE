package poomasi.domain.member.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignupRequest(
        @NotBlank(message = "이름을 작성해주세요.")
        String name,

        @NotBlank(message = "이메일을 작성해주세요.")
        @Email(message = "잘못된 이메일 형식입니다.")
        String email,

        @NotBlank(message = "비밀번호를 작성해주세요.")
        @Size(min = 8, max = 20, message = "비밀번호는 8자 ~ 20자 사이로 설정해주세요.")
        String password) {
}
