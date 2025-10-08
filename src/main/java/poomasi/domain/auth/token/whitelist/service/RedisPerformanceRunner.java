package poomasi.domain.auth.token.whitelist.service;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisPerformanceRunner implements CommandLineRunner {

    private final WhitelistRedisService whitelistRedisService;

    @Override
    public void run(String... args) throws Exception {
        // 테스트용 memberId
        Long testMemberId = 364L;

//        whitelistRedisService.deleteAllRefreshTokens();

        // 1) 더미 토큰 삽입
//         whitelistRedisService.insertDummyRefreshTokens(500_000);

        long tokenCount = whitelistRedisService.countAllRefreshTokens();
        System.out.println("현재 Redis에 저장된 refresh token 개수: " + tokenCount);

//         2) KEYS 방식 성능 측정
        long keysDuration = whitelistRedisService.measureRemoveUsingKeys(testMemberId);

//         3) SCAN 방식 성능 측정
        long scanDuration = whitelistRedisService.measureRemoveUsingScan(testMemberId+1);

        System.out.println("SCAN 방식 삭제 시간: " + scanDuration + "ms");
        System.out.println("KEYS 방식 삭제 시간: " + keysDuration + "ms");
    }
}