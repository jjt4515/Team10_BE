package poomasi.domain.auth.token.whitelist.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poomasi.global.error.BusinessException;

import java.time.Duration;
import java.util.Optional;

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

    public Optional<Long> getMemberIdByRefreshToken(final String refreshToken, Long memberId) {
        Optional<String> memberIdInWhitelist = tokenWhitelistService.getValues(refreshToken, memberId.toString());
        return memberIdInWhitelist.map(id -> {
            try {
                return Long.valueOf(id);
            } catch (NumberFormatException e) {
                return null;
            }
        });
    }

    @Transactional
    public void removeMemberRefreshToken(final Long memberId) {
        tokenWhitelistService.removeRefreshTokenById(memberId);
    }
}