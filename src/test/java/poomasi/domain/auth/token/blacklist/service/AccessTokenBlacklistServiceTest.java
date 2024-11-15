package poomasi.domain.auth.token.blacklist.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AccessTokenBlacklistServiceTest {

    @Mock
    private TokenBlacklistService tokenBlacklistService;

    @InjectMocks
    private AccessTokenBlacklistService accessTokenBlacklistService;

    private final String accessToken = "test-access-token";
    private final Long memberId = 1L;

    @Test
    @DisplayName("getMemberIdByAccessToken 성공 테스트")
    void getMemberIdByAccessToken_Success() {
        // Given
        when(tokenBlacklistService.getBlackList(accessToken)).thenReturn(Optional.of(memberId.toString()));

        // When
        Optional<Long> result = accessTokenBlacklistService.getMemberIdByAccessToken(accessToken);

        // Then
        assertTrue(result.isPresent());
        assertEquals(memberId, result.get());

        verify(tokenBlacklistService).getBlackList(accessToken);
    }

    @Test
    @DisplayName("getMemberIdByAccessToken 실패 테스트 - 블랙리스트에 토큰이 없는 경우")
    void getMemberIdByAccessToken_NotFound() {
        // Given
        when(tokenBlacklistService.getBlackList(accessToken)).thenReturn(Optional.empty());

        // When
        Optional<Long> result = accessTokenBlacklistService.getMemberIdByAccessToken(accessToken);

        // Then
        assertFalse(result.isPresent());

        verify(tokenBlacklistService).getBlackList(accessToken);
    }

    @Test
    @DisplayName("deleteAccessToken 성공 테스트")
    void deleteAccessToken_Success() {
        // When
        accessTokenBlacklistService.deleteAccessToken(accessToken);

        // Then
        verify(tokenBlacklistService).deleteBlackList(accessToken);
    }

    @Test
    @DisplayName("hasAccessToken 존재하는 토큰 확인 테스트")
    void hasAccessToken_Exists() {
        // Given
        when(tokenBlacklistService.hasKeyBlackList(accessToken)).thenReturn(true);

        // When
        boolean result = accessTokenBlacklistService.hasAccessToken(accessToken);

        // Then
        assertTrue(result);
        verify(tokenBlacklistService).hasKeyBlackList(accessToken);
    }

    @Test
    @DisplayName("hasAccessToken 존재하지 않는 토큰 확인 테스트")
    void hasAccessToken_NotExists() {
        // Given
        when(tokenBlacklistService.hasKeyBlackList(accessToken)).thenReturn(false);

        // When
        boolean result = accessTokenBlacklistService.hasAccessToken(accessToken);

        // Then
        assertFalse(result);
        verify(tokenBlacklistService).hasKeyBlackList(accessToken);
    }
}
