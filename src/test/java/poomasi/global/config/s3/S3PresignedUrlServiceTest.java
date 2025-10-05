package poomasi.global.config.s3;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import poomasi.global.config.s3.dto.response.PresignedPutUrlResponse;
import poomasi.global.util.EncryptionUtil;
import software.amazon.awssdk.http.SdkHttpMethod;
import software.amazon.awssdk.http.SdkHttpRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
class S3PresignedUrlServiceTest {

    @Mock
    private S3Presigner s3Presigner;

    @Mock
    private EncryptionUtil encryptionUtil;

    @InjectMocks
    private S3PresignedUrlService s3PresignedUrlService;

    private static final String BUCKET_NAME = "test-bucket";
    private static final String REGION = "us-west-2";
    private static final String KEY_PREFIX = "test-prefix";
    private static final String KEY_NAME = "test-key";

    @Test
    @DisplayName("Presigned Put URL 생성 성공 테스트")
    void createPresignedPutUrl_Success() throws MalformedURLException {
        // Given
        Map<String, String> metadata = new HashMap<>();
        metadata.put("key1", "value1");

        given(encryptionUtil.encodeTime(any())).willReturn("encodedTimeString");

        PresignedPutObjectRequest mockPresignedRequest = mock(PresignedPutObjectRequest.class);
        SdkHttpRequest mockHttpRequest = mock(SdkHttpRequest.class);

        // Mock httpRequest method() to return PUT
        when(mockPresignedRequest.url()).thenReturn(URI.create("https://test-bucket.s3.us-west-2.amazonaws.com/test-key").toURL());
        when(mockPresignedRequest.httpRequest()).thenReturn(mockHttpRequest);
        when(mockHttpRequest.method()).thenReturn(SdkHttpMethod.PUT);

        given(s3Presigner.presignPutObject(any(PutObjectPresignRequest.class)))
                .willReturn(mockPresignedRequest);

        // When
        PresignedPutUrlResponse response = s3PresignedUrlService.createPresignedPutUrl(BUCKET_NAME, REGION, KEY_PREFIX);

        // Then
        assertEquals("https://test-bucket.s3.us-west-2.amazonaws.com/test-key", response.presignedPutUrl());
        assertEquals("https://test-bucket.s3.us-west-2.amazonaws.com/" + response.keyName(), response.objectUrl());

        verify(s3Presigner).presignPutObject(any(PutObjectPresignRequest.class));
    }

    @Test
    @DisplayName("Presigned Get URL 생성 성공 테스트")
    void createPresignedGetUrl_Success() throws MalformedURLException {
        // Given
        PresignedGetObjectRequest mockPresignedRequest = mock(PresignedGetObjectRequest.class);
        SdkHttpRequest mockHttpRequest = mock(SdkHttpRequest.class);

        // Mock httpRequest method() to return GET
        when(mockPresignedRequest.url()).thenReturn(URI.create("https://test-bucket.s3.us-west-2.amazonaws.com/test-key").toURL());
        when(mockPresignedRequest.httpRequest()).thenReturn(mockHttpRequest);
        when(mockHttpRequest.method()).thenReturn(SdkHttpMethod.GET);

        given(s3Presigner.presignGetObject(any(GetObjectPresignRequest.class)))
                .willReturn(mockPresignedRequest);

        // When
        String presignedUrl = s3PresignedUrlService.createPresignedGetUrl(BUCKET_NAME, KEY_NAME);

        // Then
        assertEquals("https://test-bucket.s3.us-west-2.amazonaws.com/test-key", presignedUrl);

        verify(s3Presigner).presignGetObject(any(GetObjectPresignRequest.class));
    }
}
