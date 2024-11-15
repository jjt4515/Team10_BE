package poomasi.global.ocr.dto.request;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class NaverOcrRequest extends OcrRequest {
    private String version;
    private String requestId;
    private long timestamp;
    private String lang;
    private List<NaverOcrImage> images;

    @Builder
    public NaverOcrRequest(List<NaverOcrImage> images) {
        this.version = "V2";
        this.requestId = "string";
        this.timestamp = System.currentTimeMillis();
        this.lang = "ko";
        this.images = images;
    }
}
