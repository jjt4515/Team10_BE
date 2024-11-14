package poomasi.global.ocr.dto.response;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@RequiredArgsConstructor
public class NaverOcrResponse extends OcrResponse {
    private String version;
    private String requestId;
    private long timestamp;
    private List<NaverOcrImageResponse> images;

    @Builder
    public NaverOcrResponse(String version, String requestId, long timestamp, List<NaverOcrImageResponse> images) {
        this.version = version;
        this.requestId = requestId;
        this.timestamp = timestamp;
        this.images = images;
    }
}
