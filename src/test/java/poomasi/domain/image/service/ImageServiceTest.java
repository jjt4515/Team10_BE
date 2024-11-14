package poomasi.domain.image.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import poomasi.domain.image.dto.request.ImageRequest;
import poomasi.domain.image.dto.response.ImageResponse;
import poomasi.domain.image.entity.Image;
import poomasi.domain.image.entity.ImageType;
import poomasi.domain.image.repository.ImageRepository;
import poomasi.domain.image.validator.ImageOwnerValidatorFactory;
import poomasi.domain.image.linker.ImageLinkerFactory;
import poomasi.domain.image.deleteLinker.ImageDeleteFactory;
import poomasi.domain.member.entity.Member;
import poomasi.domain.member.service.MemberService;
import poomasi.global.error.BusinessException;

import java.util.Optional;

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
    }

    @Test
    @DisplayName("이미지 저장 성공 테스트")
    void saveImage_ShouldSaveImage_WhenValidData() {
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
//
//    @Test
//    void saveImage_ShouldThrowException_WhenImageLimitExceeded() {
//        // Given
//        Long memberId = 1L;
//        ImageRequest imageRequest = new ImageRequest("key", "http://url.com", ImageType.MEMBER_PROFILE, 1L);
//
//        // Simulate the image limit being exceeded for MEMBER_PROFILE.
//        when(imageRepository.countByTypeAndReferenceIdAndDeletedAtIsNull(any(), any()))
//                .thenReturn(1);  // Simulate limit being exceeded
//
//        // When / Then
//        BusinessException exception = assertThrows(BusinessException.class, () ->
//                imageService.saveImage(memberId, imageRequest)
//        );
//        assertEquals("Image limit exceeded", exception.getMessage());
//    }
//
//    @Test
//    void validateImageOwner_ShouldThrowException_WhenOwnerIsNotValid() {
//        // Given
//        Long memberId = 1L;
//        ImageRequest imageRequest = new ImageRequest("key", "http://url.com", ImageType.MEMBER_PROFILE, 1L);
//
//        // Simulate owner validation failure.
//        ImageOwnerValidator validator = mock(ImageOwnerValidator.class);
//        when(validatorFactory.getValidator(any())).thenReturn(validator);
//        when(validator.validateOwner(any(), any())).thenReturn(false);
//
//        // When / Then
//        BusinessException exception = assertThrows(BusinessException.class, () ->
//                imageService.saveImage(memberId, imageRequest)
//        );
//        assertEquals("Image owner mismatch", exception.getMessage());
//    }
//
//    @Test
//    void deleteImage_ShouldDeleteImage_WhenValidImage() {
//        // Given
//        Long memberId = 1L;
//        Long imageId = 1L;
//        Image image = new Image("key", "http://url.com", ImageType.MEMBER_PROFILE, 1L);
//        ImageResponse imageResponse = new ImageResponse(1L, "key", "http://url.com", ImageType.MEMBER_PROFILE, 1L);
//
//        // Mock the repository to simulate image being found.
//        when(imageRepository.findByIdAndDeletedAtIsNull(imageId))
//                .thenReturn(Optional.of(image));  // Image exists
//
//        // When
//        imageService.deleteImage(memberId, imageId);
//
//        // Then
//        verify(imageRepository, times(1)).delete(any(Image.class));  // Verify delete is called
//    }
//
//    @Test
//    void deleteImage_ShouldThrowException_WhenImageNotFound() {
//        // Given
//        Long memberId = 1L;
//        Long imageId = 1L;
//
//        // Mock the repository to simulate image not found.
//        when(imageRepository.findByIdAndDeletedAtIsNull(imageId))
//                .thenReturn(Optional.empty());  // Image not found
//
//        // When / Then
//        BusinessException exception = assertThrows(BusinessException.class, () ->
//                imageService.deleteImage(memberId, imageId)
//        );
//        assertEquals("Image not found", exception.getMessage());
//    }
//
//    @Test
//    void updateImage_ShouldUpdateImage_WhenValidData() {
//        // Given
//        Long memberId = 1L;
//        Long imageId = 1L;
//        ImageRequest imageRequest = new ImageRequest("newKey", "http://newurl.com", ImageType.MEMBER_PROFILE, 1L);
//
//        Image existingImage = new Image("key", "http://url.com", ImageType.MEMBER_PROFILE, 1L);
//        when(imageRepository.findByIdAndDeletedAtIsNull(imageId))
//                .thenReturn(Optional.of(existingImage));  // Simulate existing image
//
//        when(imageRepository.save(any(Image.class)))
//                .thenReturn(existingImage);  // Simulate image being updated
//
//        // When
//        ImageResponse imageResponse = imageService.updateImage(memberId, imageId, imageRequest);
//
//        // Then
//        assertNotNull(imageResponse);
//        assertEquals(imageRequest.objectKey(), imageResponse.objectKey());
//        verify(imageRepository, times(1)).save(any(Image.class));  // Verify save is called
//    }
}
