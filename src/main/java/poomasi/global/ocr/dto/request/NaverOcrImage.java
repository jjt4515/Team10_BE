package poomasi.global.ocr.dto.request;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import poomasi.global.common.ImageFormat;

import java.util.List;
import java.util.Locale;

@Data
@NoArgsConstructor
public class NaverOcrImage {
    private String format;
    private String name;
    private String data;
    private String url;
    private List<Integer> templateIds;

    @Builder
    public NaverOcrImage(ImageFormat format, String url, List<Integer> templateIds) {
        this.format = format.name().toLowerCase(Locale.ROOT);// png로 설정
        this.name = "medium";
        this.url = url;
        this.templateIds = templateIds;
    }
}
