package poomasi.domain.member._profile.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import poomasi.domain.member._profile.entity.MemberProfile;
import poomasi.domain.member._profile.repository.MemberProfileRepository;
import poomasi.global.error.BusinessError;
import poomasi.global.error.BusinessException;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class MemberProfileServiceTest {

    @Mock
    private MemberProfileRepository memberProfileRepository;

    @InjectMocks
    private MemberProfileService memberProfileService;

    private MemberProfile memberProfile;

    @BeforeEach
    void setup() {
        memberProfile = mock(MemberProfile.class);
    }

    @Test
    @DisplayName("getMemberProfileById 성공 테스트")
    void getMemberProfileById_Success() {
        // Given
        when(memberProfile.getId()).thenReturn(1L);
        when(memberProfile.getPhoneNumber()).thenReturn("123456789");
        when(memberProfileRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(memberProfile));

        // When
        MemberProfile result = memberProfileService.getMemberProfileById(1L);

        // Then
        assertEquals(1L, result.getId());
        assertEquals("123456789", result.getPhoneNumber());

        verify(memberProfileRepository).findByIdAndDeletedAtIsNull(1L);
    }

    @Test
    @DisplayName("getMemberProfileById 실패 테스트 - 프로필을 찾을 수 없는 경우")
    void getMemberProfileById_NotFound() {
        // Given
        when(memberProfileRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> memberProfileService.getMemberProfileById(1L))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("businessError", BusinessError.MEMBER_PROFILE_NOT_FOUND);


        verify(memberProfileRepository).findByIdAndDeletedAtIsNull(1L);
    }

    @Test
    @DisplayName("saveMemberProfile 성공 테스트")
    void saveMemberProfile_Success() {
        // Given
        when(memberProfile.getId()).thenReturn(1L);
        when(memberProfile.getPhoneNumber()).thenReturn("123456789");
        when(memberProfileRepository.save(any(MemberProfile.class))).thenReturn(memberProfile);

        // When
        MemberProfile result = memberProfileService.saveMemberProfile(memberProfile);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("123456789", result.getPhoneNumber());

        verify(memberProfileRepository).save(memberProfile);
    }
}
