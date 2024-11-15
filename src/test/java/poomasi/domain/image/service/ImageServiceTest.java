package poomasi.domain.image.service;

import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import poomasi.domain.image.dto.request.ImageRequest;
import poomasi.domain.image.dto.response.ImageResponse;
import poomasi.domain.image.entity.Image;
import poomasi.domain.image.entity.ImageType;
import poomasi.domain.image.repository.ImageRepository;
import poomasi.domain.image.validator.ImageOwnerValidator;
import poomasi.domain.image.validator.ImageOwnerValidatorFactory;
import poomasi.domain.image.linker.ImageLinkerFactory;
import poomasi.domain.image.deleteLinker.ImageDeleteFactory;
import poomasi.domain.member.entity.Member;
import poomasi.domain.member.service.MemberService;
import poomasi.global.error.BusinessError;
import poomasi.global.error.BusinessException;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class ImageServiceTest {

    @Mock
    private ImageRepository imageRepository;

    @Mock
    private MemberService memberService;

    @Mock
    private ImageOwnerValidatorFactory validatorFactory;

    @Mock
    private ImageLinkerFactory imageLinkerFactory;

    @Mock
    private ImageDeleteFactory imageDeleteFactory;

    @InjectMocks
    private ImageService imageService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("이미지 저장 성공 테스트")
    void saveImage_success() {
        // Given
        Long memberId = 1L;
        ImageRequest imageRequest = new ImageRequest("key", "imageUrl", ImageType.MEMBER_PROFILE, 1L);
        Image image = new Image("key", "imageUrl", ImageType.MEMBER_PROFILE, 1L);

        when(imageRepository.findByObjectKeyAndTypeAndReferenceId(any(), any(), any()))
                .thenReturn(Optional.empty());
        when(imageRepository.save(any(Image.class)))
                .thenReturn(image);

        Member member = mock(Member.class);
        when(memberService.findMemberById(memberId)).thenReturn(member);
        when(member.isAdmin()).thenReturn(true);

        // When
        ImageResponse imageResponse = imageService.saveImage(memberId, imageRequest);

        // Then
        assertNotNull(imageResponse);
        assertEquals(image.getObjectKey(), imageResponse.objectKey());
        verify(imageRepository, times(1)).save(any(Image.class));
    }

    @Test
    @DisplayName("이미지 저장 실패 테스트 - 개수 초과")
    void saveImage_IMAGE_LIMIT_EXCEED() {
        // Given
        Long memberId = 1L;
        ImageRequest imageRequest = new ImageRequest("key", "imageUrl", ImageType.MEMBER_PROFILE, 1L);

        when(imageRepository.countByTypeAndReferenceIdAndDeletedAtIsNull(any(), any()))
                .thenReturn(1L);

        Member member = mock(Member.class);
        when(memberService.findMemberById(memberId)).thenReturn(member);

        when(imageRepository.countByTypeAndReferenceIdAndDeletedAtIsNull(any(), any()))
                .thenReturn(1L);

        // When & Then
        assertThatThrownBy(() -> imageService.saveImage(memberId, imageRequest))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("businessError", BusinessError.IMAGE_LIMIT_EXCEED);
    }

    @Test
    @DisplayName("이미지 소유자 검증 실패 테스트")
    void validateImageOwner_IMAGE_OWNER_MISMATCH() {
        // Given
        Long memberId = 1L;
        ImageRequest imageRequest = new ImageRequest("key", "imageUrl", ImageType.MEMBER_PROFILE, 1L);

        Member member = mock(Member.class);
        when(memberService.findMemberById(memberId)).thenReturn(member);
        when(member.isAdmin()).thenReturn(false);

        ImageOwnerValidator validator = mock(ImageOwnerValidator.class);
        when(validatorFactory.getValidator(any())).thenReturn(validator);
        when(validator.validateOwner(any(), any())).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> imageService.saveImage(memberId, imageRequest))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("businessError", BusinessError.IMAGE_OWNER_MISMATCH);
    }

    @Test
    @DisplayName("이미지 삭제 성공 테스트")
    void deleteImage_success() {
        // Given
        Long memberId = 1L;
        Long imageId = 1L;
        Image image = new Image("key", "imageUrl", ImageType.MEMBER_PROFILE, 1L);

        Member member = mock(Member.class);
        when(memberService.findMemberById(memberId)).thenReturn(member);
        when(member.isAdmin()).thenReturn(false);

        when(imageRepository.findByIdAndDeletedAtIsNull(imageId))
                .thenReturn(Optional.of(image));

        // When
        imageService.deleteImage(memberId, imageId);

        // Then
        verify(imageRepository, times(1)).delete(any(Image.class));
    }

    @Test
    @DisplayName("이미지 삭제 실패 테스트 - 이미지 없음")
    void deleteImage_IMAGE_NOT_FOUND() {
        // Given
        Long memberId = 1L;
        Long imageId = 1L;

        when(imageRepository.findByIdAndDeletedAtIsNull(imageId))
                .thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> imageService.deleteImage(memberId, imageId))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("businessError", BusinessError.IMAGE_NOT_FOUND);
    }

    @Test
    @DisplayName("이미지 업데이트 성공 테스트")
    void updateImage_success() {
        // Given
        Long memberId = 1L;
        Long imageId = 1L;
        ImageRequest imageRequest = new ImageRequest("newKey", "newImageUrl", ImageType.MEMBER_PROFILE, 1L);

        Member member = mock(Member.class);
        when(memberService.findMemberById(memberId)).thenReturn(member);
        when(member.isAdmin()).thenReturn(false);

        Image existingImage = new Image("key", "imageUrl", ImageType.MEMBER_PROFILE, 1L);
        when(imageRepository.findByIdAndDeletedAtIsNull(imageId))
                .thenReturn(Optional.of(existingImage));

        when(imageRepository.save(any(Image.class)))
                .thenReturn(existingImage);

        // When
        ImageResponse imageResponse = imageService.updateImage(memberId, imageId, imageRequest);

        // Then
        assertNotNull(imageResponse);
        assertEquals(imageRequest.objectKey(), "newKey");
        assertEquals(imageResponse.objectKey(), "key");
        verify(imageRepository, times(1)).save(any(Image.class));
    }

    @Test
    @DisplayName("이미지 수정 실패 - 자신의 이미지가 아님")
    void updateImage_IMAGE_OWNER_MISMATCH() {
        // Given
        Long memberId = 1L;
        Long imageId = 1L;
        ImageRequest imageRequest = new ImageRequest("newKey", "newImageUrl", ImageType.PRODUCT, 4L);

        Member member = mock(Member.class);
        when(memberService.findMemberById(memberId)).thenReturn(member);
        when(member.isAdmin()).thenReturn(false);

        ImageOwnerValidator validator = mock(ImageOwnerValidator.class);
        when(validatorFactory.getValidator(any())).thenReturn(validator);
        when(validator.validateOwner(any(), any())).thenReturn(false);

        Image existingImage = new Image("key", "imageUrl", ImageType.MEMBER_PROFILE, 1L);
        when(imageRepository.findByIdAndDeletedAtIsNull(imageId))
                .thenReturn(Optional.of(existingImage));

        when(imageRepository.save(any(Image.class)))
                .thenReturn(existingImage);

        assertThatThrownBy(() -> imageService.updateImage(memberId, imageId, imageRequest))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("businessError", BusinessError.IMAGE_OWNER_MISMATCH);
    }
}
