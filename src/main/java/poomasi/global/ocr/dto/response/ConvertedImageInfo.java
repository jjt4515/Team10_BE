package poomasi.global.ocr.dto.response;

import lombok.Data;

@Data
public class ConvertedImageInfo {
    private int width;
    private int height;
    private int pageIndex;
    private boolean longImage;
}
