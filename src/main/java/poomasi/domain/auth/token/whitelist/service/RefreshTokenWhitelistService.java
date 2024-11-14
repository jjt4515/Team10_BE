package poomasi.domain.auth.token.whitelist.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poomasi.global.error.BusinessException;

import java.time.Duration;

import static poomasi.global.error.BusinessError.REFRESH_TOKEN_NOT_FOUND;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RefreshTokenWhitelistService {

    private final TokenWhitelistService tokenWhitelistService;

    @Value("${jwt.refresh-token-expiration-time}")
    private long REFRESH_TOKEN_EXPIRE_TIME;

    @Transactional
    public void putRefreshToken(final String refreshToken, Long memberId) {
        tokenWhitelistService.setValues(refreshToken, memberId.toString(), Duration.ofSeconds(REFRESH_TOKEN_EXPIRE_TIME));
    }

    public Long getRefreshToken(final String refreshToken, Long memberId) {
        String result = tokenWhitelistService.getValues(refreshToken, memberId.toString())
                .orElseThrow(() -> new BusinessException(REFRESH_TOKEN_NOT_FOUND));
        return Long.parseLong(result);
    }

    @Transactional
    public void removeMemberRefreshToken(final Long memberId) {
        tokenWhitelistService.removeRefreshTokenById(memberId);
    }
}