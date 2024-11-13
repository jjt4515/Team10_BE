package poomasi.global.ocr.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class NaverOcrResponse extends OcrResponse {
    private String version;
    private String requestId;
    private long timestamp;
    private List<NaverOcrImageResponse> images;
}
