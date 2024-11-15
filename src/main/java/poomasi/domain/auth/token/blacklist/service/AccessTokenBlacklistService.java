package poomasi.domain.auth.token.blacklist.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AccessTokenBlacklistService {

    private final TokenBlacklistService tokenBlacklistService;

    @Value("${jwt.access-token-expiration-time}")
    long ACCESS_TOKEN_EXPIRE_TIME;

    @Transactional
    public void putAccessToken(final String accessToken, Long memberId) {
        tokenBlacklistService.setBlackList(accessToken, memberId.toString(), Duration.ofSeconds(ACCESS_TOKEN_EXPIRE_TIME));
    }

    public Optional<Long> getMemberIdByAccessToken(final String accessToken) {
        Optional<String> memberIdInBlacklist = tokenBlacklistService.getBlackList(accessToken);
        return memberIdInBlacklist.map(id -> {
            try {
                return Long.valueOf(id);
            } catch (NumberFormatException e) {
                return null;
            }
        });
    }

    @Transactional
    public void deleteAccessToken(String accessToken) {
        tokenBlacklistService.deleteBlackList(accessToken);
    }

    public boolean hasAccessToken(String accessToken) {
        return tokenBlacklistService.hasKeyBlackList(accessToken);
    }

}