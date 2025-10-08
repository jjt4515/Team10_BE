package poomasi.domain.auth.token.blacklist.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class AccessTokenBlacklistService {

    private final TokenBlacklistService tokenBlacklistService;

    @Value("${jwt.access-token-expiration-time}")
    private long ACCESS_TOKEN_EXPIRE_TIME;

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

    @Transactional
    public String insertTokenToBlacklist(){
        int count = 50000;

        for (long i = 1; i <= count; i++) {
            String fakeToken = "test-token" + i;
            if (hasAccessToken(fakeToken))
                continue;
            putAccessToken(fakeToken, i);
        }

        return count + "개의 테스트 토큰이 블랙리스트에 저장되었습니다.";
    }

    public String countTokenInBlacklist() {
        return String.valueOf(tokenBlacklistService.countAll());
    }

    @Transactional
    public void deleteTokenToBlacklist(){
        int count = 10000;

        for (long i = 1; i <= count; i++) {
            String fakeToken = "test-token";
            if (hasAccessToken(fakeToken)) {
                deleteAccessToken(fakeToken);
            }
        }
    }

}
