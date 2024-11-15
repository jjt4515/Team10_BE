package poomasi.domain.auth.token.whitelist.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poomasi.domain.auth.token.whitelist.entity.Whitelist;
import poomasi.domain.auth.token.whitelist.repository.WhitelistRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WhitelistJpaService implements TokenWhitelistService {

    private final WhitelistRepository whitelistRepository;

    @Override
    @Transactional
    public void setValues(String key, String data, Duration duration) {
        Whitelist tokenEntity = new Whitelist();
        tokenEntity.setTokenKey(key);
        tokenEntity.setData(data);
        tokenEntity.setExpireAt(LocalDateTime.now().plusSeconds(duration.getSeconds()));
        whitelistRepository.save(tokenEntity);
    }

    @Override
    public Optional<String> getValues(String key, String data) {
        return whitelistRepository.findByTokenKeyAndExpireAtAfter(key, LocalDateTime.now())
                .map(Whitelist::getData);
    }

    @Override
    @Transactional
    public void removeRefreshTokenById(final Long memberId) {
        whitelistRepository.deleteAllByData(String.valueOf(memberId));
    }

    @Transactional
    public void removeExpiredTokens() {
        whitelistRepository.deleteAllByExpireAtBefore(LocalDateTime.now());
    }
}
