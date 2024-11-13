package poomasi.global.ocr;

import poomasi.global.ocr.dto.request.OcrRequest;
import poomasi.global.ocr.dto.response.OcrResponse;

public interface OcrService {
    OcrResponse extractTextFromImage(OcrRequest request);
}
