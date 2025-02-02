package poomasi.domain.auth.token.whitelist.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import poomasi.domain.auth.token.whitelist.service.WhitelistJpaService;
import poomasi.domain.auth.token.whitelist.service.WhitelistRedisService;
import poomasi.domain.auth.token.whitelist.service.TokenWhitelistService;

@Configuration
public class TokenWhitelistServiceConfig {

    @Value("${spring.token.storage.type}")
    private String tokenStorageType; 

    @Bean
    public TokenWhitelistService tokenWhitelistService(WhitelistRedisService whitelistRedisService, WhitelistJpaService whitelistJpaService) {
        if ("redis".equals(tokenStorageType)) {
            return whitelistRedisService;
        } else {
            return whitelistJpaService;
        }
    }
}