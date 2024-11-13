package poomasi.global.config.s3.dto.request;

import jakarta.validation.constraints.NotBlank;

import java.util.Map;

public record PresignedUrlPutRequest(
        @NotBlank(message = "키 접두어를 입력해주세요")
        String keyPrefix,

        Map<String, String> metadata) {
}
