package poomasi.global.ocr.dto.response;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class NaverOcrImageResponse {
    private String uid;
    private String name;
    private String inferResult;
    private String message;
    private ValidationResult validationResult;
    private ConvertedImageInfo convertedImageInfo;

    @Builder
    public NaverOcrImageResponse(String uid, String name, String inferResult, String message, ValidationResult validationResult, ConvertedImageInfo convertedImageInfo) {
        this.uid = uid;
        this.name = name;
        this.inferResult = inferResult;
        this.message = message;
        this.validationResult = validationResult;
        this.convertedImageInfo = convertedImageInfo;
    }
}
