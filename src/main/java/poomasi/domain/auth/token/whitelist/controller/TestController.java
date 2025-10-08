package poomasi.domain.auth.token.whitelist.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import poomasi.domain.auth.token.whitelist.service.WhitelistRedisService;

@RestController
@RequiredArgsConstructor
public class TestController {
    private final WhitelistRedisService whitelistRedisService;

    @GetMapping("/test-logout-scan/{memberId}")
    public ResponseEntity<String> testLogoutScan(@PathVariable Long memberId) {
        long duration = whitelistRedisService.measureRemoveUsingScan(memberId);
        return ResponseEntity.ok("✅ Scan 방식 (" + memberId + ") - 삭제 시간: " + duration + "ms");
    }

    @GetMapping("/test-logout-key/{memberId}")
    public ResponseEntity<String> testLogoutKey(@PathVariable Long memberId) {
        long duration = whitelistRedisService.measureRemoveUsingKeys(memberId);
        return ResponseEntity.ok("✅ Keys 방식 (" + memberId + ") - 삭제 시간: " + duration + "ms");
    }
}


