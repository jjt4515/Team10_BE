package poomasi.domain.auth.token.whitelist.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import poomasi.domain.auth.token.whitelist.entity.Whitelist;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface WhitelistRepository extends JpaRepository<Whitelist, Long> {
    void deleteAllByData(String Data);
    void deleteAllByExpireAtBefore(LocalDateTime now);
    Optional<Whitelist> findByTokenKeyAndExpireAtAfter(String key, LocalDateTime now);
}