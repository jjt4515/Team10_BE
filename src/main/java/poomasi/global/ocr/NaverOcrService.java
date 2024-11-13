package poomasi.global.ocr;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import poomasi.global.error.ApplicationException;
import poomasi.global.ocr.dto.request.NaverOcrRequest;
import poomasi.global.ocr.dto.request.OcrRequest;
import poomasi.global.ocr.dto.response.NaverOcrResponse;
import poomasi.global.ocr.dto.response.OcrResponse;

import static poomasi.global.error.ApplicationError.OCR_SUPPORT_ERROR;


@Service
@RequiredArgsConstructor
public class NaverOcrService implements OcrService {
    @Value("${naver.ocr.secret}")
    private String ocrSecret;

    @Value("${naver.ocr.invoke}")
    private String ocrInvoke;

    @Value("${naver.ocr.template}")
    private String ocrTemplate;

    private final RestClient.Builder restClient;

    @Override
    public OcrResponse extractTextFromImage(OcrRequest request) {
        if (!(request instanceof NaverOcrRequest)) {
            throw new ApplicationException(OCR_SUPPORT_ERROR);
        }

        NaverOcrRequest ocrRequest = (NaverOcrRequest) request;

        return restClient.build()
                .post()
                .uri(ocrInvoke)
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .header("X-OCR-SECRET", ocrSecret)
                .body(ocrRequest)
                .retrieve()
                .body(NaverOcrResponse.class);

    }
}
