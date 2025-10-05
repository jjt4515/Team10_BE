package poomasi.domain.auth.token.whitelist.service;

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
class RefreshTokenWhitelistServiceTest {

    @Mock
    private TokenWhitelistService tokenWhitelistService;

    @InjectMocks
    private RefreshTokenWhitelistService refreshTokenWhitelistService;

    private final String refreshToken = "test-refresh-token";
    private final Long memberId = 1L;

    @Test
    @DisplayName("getMemberIdByRefreshToken 성공 테스트")
    void getMemberIdByRefreshToken_Success() {
        // Given
        when(tokenWhitelistService.getValues(refreshToken, memberId.toString())).thenReturn(Optional.of(memberId.toString()));

        // When
        Optional<Long> result = refreshTokenWhitelistService.getMemberIdByRefreshToken(refreshToken, memberId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(memberId, result.get());

        verify(tokenWhitelistService).getValues(refreshToken, memberId.toString());
    }

    @Test
    @DisplayName("getMemberIdByRefreshToken 실패 테스트 - 화이트리스트에 토큰이 없는 경우")
    void getMemberIdByRefreshToken_NotFound() {
        // Given
        when(tokenWhitelistService.getValues(refreshToken, memberId.toString())).thenReturn(Optional.empty());

        // When
        Optional<Long> result = refreshTokenWhitelistService.getMemberIdByRefreshToken(refreshToken, memberId);

        // Then
        assertFalse(result.isPresent());

        verify(tokenWhitelistService).getValues(refreshToken, memberId.toString());
    }

    @Test
    @DisplayName("removeMemberRefreshToken 성공 테스트")
    void removeMemberRefreshToken_Success() {
        // When
        refreshTokenWhitelistService.removeMemberRefreshToken(memberId);

        // Then
        verify(tokenWhitelistService).removeRefreshTokenById(memberId);
    }
}
