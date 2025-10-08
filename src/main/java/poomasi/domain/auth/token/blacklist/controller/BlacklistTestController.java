package poomasi.domain.auth.token.blacklist.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import poomasi.domain.auth.token.blacklist.service.AccessTokenBlacklistService;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class BlacklistTestController {

    private final AccessTokenBlacklistService accessTokenBlacklistService;

    @PostMapping("/insert-blacklist")
    public String insertBlacklistTokens() {
        return accessTokenBlacklistService.insertTokenToBlacklist();
    }

    @PostMapping("/delete-blacklist")
    public void deleteBlacklistTokens() {
        accessTokenBlacklistService.deleteTokenToBlacklist();
    }

    @GetMapping("/count-blacklist")
    public String countBlacklistTokens() {
        return accessTokenBlacklistService.countTokenInBlacklist();
    }

    @PostMapping("/is-in-blacklist")
    public boolean isTokenInBlacklist(@RequestBody String accessToken) {
        return accessTokenBlacklistService.hasAccessToken(accessToken);
    }
}
