package poomasi.domain.auth.token.whitelist.service;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisPerformanceRunner implements CommandLineRunner {

    private final WhitelistRedisService whitelistRedisService;

    @Override
    public void run(String... args) throws Exception {
        // 테스트용 memberId
        Long testMemberId = 42L;

        // 1) 더미 토큰 충분히 삽입 (최초 1회만 실행)
        // whitelistRedisService.insertDummyRefreshTokens(100_000);

        // 2) KEYS 방식 성능 측정
        long keysDuration = whitelistRedisService.measureRemoveUsingKeys(testMemberId);

        // 3) SCAN 방식 성능 측정
        long scanDuration = whitelistRedisService.measureRemoveUsingScan(testMemberId);

        System.out.println("KEYS 방식 삭제 시간: " + keysDuration + "ms");
        System.out.println("SCAN 방식 삭제 시간: " + scanDuration + "ms");
    }
}

