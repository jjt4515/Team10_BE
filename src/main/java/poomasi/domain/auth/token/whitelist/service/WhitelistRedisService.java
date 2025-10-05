package poomasi.domain.auth.token.whitelist.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poomasi.global.config.redis.error.RedisOperationException;

import java.time.Duration;
import java.util.*;

import static poomasi.global.config.redis.error.RedisExceptionHandler.handleRedisException;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WhitelistRedisService implements TokenWhitelistService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisConnectionFactory redisConnectionFactory;

    @Transactional
    public void setValues(String key, String data, Duration duration) {
        String redisKey = generateKey(data, key);
        handleRedisException(() -> {
            ValueOperations<String, Object> values = redisTemplate.opsForValue();
            values.set(redisKey, data, duration);
            return null;
        }, "Redis에 값을 설정하는 중 오류 발생: " + redisKey);
    }

    public Optional<String> getValues(String key, String data) {
        String redisKey = generateKey(data, key);
        return handleRedisException(() -> {
            ValueOperations<String, Object> values = redisTemplate.opsForValue();
            Object result = values.get(redisKey);
            return Optional.ofNullable(result).map(Object::toString);
        }, "Redis에서 값을 가져오는 중 오류 발생: " + redisKey);
    }

    @Transactional
    public void removeRefreshTokenById(Long memberId) {
        List<String> keys = scanKeysByPattern(generateKey(String.valueOf(memberId), "*"));
        for (String key : keys) {
            deleteValues(key, memberId.toString());
        }
    }

    @Transactional
    public void deleteValues(String key, String data) {
        String redisKey = generateKey(data, key);
        handleRedisException(() -> redisTemplate.delete(redisKey), "Redis에서 값을 삭제하는 중 오류 발생: " + redisKey);
    }

    public List<String> scanKeysByPattern(String pattern) {
        return handleRedisException(() -> {
            List<String> keys = new ArrayList<>();
            ScanOptions options = ScanOptions.scanOptions().match(pattern).count(100).build();

            try (RedisConnection connection = redisConnectionFactory.getConnection()) {
                Cursor<byte[]> cursor = connection.scan(options);
                while (cursor.hasNext()) {
                    keys.add(new String(cursor.next()));
                }
            } catch (Exception e) {
                throw new RedisOperationException("Redis SCAN 중 오류 발생");
            }
            return keys;
        }, "SCAN 중 오류 발생: " + pattern);
    }

    public boolean hasKey(String key, String data) {
        String redisKey = generateKey(data, key);
        return handleRedisException(() -> Boolean.TRUE.equals(redisTemplate.hasKey(redisKey)), "Redis에서 키 존재 여부 확인 중 오류 발생: " + redisKey);
    }

    public List<String> getKeysByPattern(String pattern) {
        Set<String> keys = redisTemplate.keys(pattern);
        return keys != null ? new ArrayList<>(keys) : Collections.emptyList();
    }

    public List<String> getKeysByPatternAndGetValues(String pattern) {
        Set<String> keys = redisTemplate.keys(pattern);
        List<String> result = new ArrayList<>();
        if (keys != null) {
            ValueOperations<String, Object> ops = redisTemplate.opsForValue();
            for (String key : keys) {
                Object value = ops.get(key);  // 실제 값 조회
                // 필요 시 value.toString() 확인 가능
                result.add(key); // 값은 안 써도 되면 키만 추가
            }
        }
        return result;
    }

    private String generateKey(String memberId, String token) {
        return "refreshToken:" + memberId + ":" + token;
    }

    // ✅ 성능 비교용 메서드들 추가

    @Transactional
    public void insertDummyRefreshTokens(int count) {
        log.info("🧪 더미 토큰 {}개 삽입 시작", count);
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
        for (int i = 0; i < count; i++) {
            String token = "dummyToken" + i;
            String memberId = String.valueOf(i % 100); // 중복된 멤버 ID
            String redisKey = generateKey(memberId, token);
            ops.set(redisKey, memberId, Duration.ofHours(1));
        }
        log.info("✅ 더미 토큰 삽입 완료");
    }

    public void compareScanAndKeysPerformance() {
        String pattern = "refreshToken:*";

        // KEYS 방식
        long startKeys = System.currentTimeMillis();
        List<String> keysResult = getKeysByPatternAndGetValues(pattern);
        long endKeys = System.currentTimeMillis();
        log.info("🔑 KEYS 방식 - 조회된 키 개수: {}, 소요 시간: {}ms", keysResult.size(), (endKeys - startKeys));

        // SCAN 방식
        long startScan = System.currentTimeMillis();
        List<String> scanResult = scanKeysByPattern(pattern);
        long endScan = System.currentTimeMillis();
        log.info("🔍 SCAN 방식 - 조회된 키 개수: {}, 소요 시간: {}ms", scanResult.size(), (endScan - startScan));
    }

    // KEYS 방식으로 로그아웃 시 토큰 삭제
    @Transactional
    public void removeRefreshTokenByIdUsingKeys(Long memberId) {
        String pattern = generateKey(String.valueOf(memberId), "*");
        Set<String> keys = redisTemplate.keys(pattern);
        if (keys != null) {
            for (String key : keys) {
                redisTemplate.delete(key);
            }
        }
    }

    // SCAN 방식으로 로그아웃 시 토큰 삭제 (기존 방식)
    @Transactional
    public void removeRefreshTokenByIdUsingScan(Long memberId) {
        List<String> keys = scanKeysByPattern(generateKey(String.valueOf(memberId), "*"));
        for (String key : keys) {
            redisTemplate.delete(key);
        }
    }

    // KEYS 방식 성능 측정
    public long measureRemoveUsingKeys(Long memberId) {
        long start = System.currentTimeMillis();
        removeRefreshTokenByIdUsingKeys(memberId);
        long end = System.currentTimeMillis();
        long duration = end - start;
        log.info("🔑 KEYS 방식 removeRefreshTokenById({}) 소요 시간: {}ms", memberId, duration);
        return duration;
    }

    // SCAN 방식 성능 측정
    public long measureRemoveUsingScan(Long memberId) {
        long start = System.currentTimeMillis();
        removeRefreshTokenByIdUsingScan(memberId);
        long end = System.currentTimeMillis();
        long duration = end - start;
        log.info("🔍 SCAN 방식 removeRefreshTokenById({}) 소요 시간: {}ms", memberId, duration);
        return duration;
    }
}
