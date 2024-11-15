package poomasi.domain.auth.token.whitelist.service;

import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
public interface TokenWhitelistService {
    void setValues(String key, String data, Duration duration);
    Optional<String> getValues(String key, String data);
    void removeRefreshTokenById(final Long memberId);
}