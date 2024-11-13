package poomasi.global.ocr.dto.response;

import lombok.Data;

@Data
public class NaverOcrImageResponse {
    private String uid;
    private String name;
    private String inferResult;
    private String message;
    private ValidationResult validationResult;
    private ConvertedImageInfo convertedImageInfo;
}
