package poomasi.global.ocr.dto.request;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class NaverOcrRequest extends OcrRequest {
    private String version;
    private String requestId;
    private long timestamp;
    private String lang;
    private List<NaverOcrImage> images;
}
