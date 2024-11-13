package poomasi.global.ocr.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class NaverOcrImage {
    private String format;
    private String name;
    private String data;
    private String url;
    private List<Integer> templateIds;
}
