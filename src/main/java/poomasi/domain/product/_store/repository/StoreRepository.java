package poomasi.domain.product._store.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import poomasi.domain.product._store.entity.Store;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {

    @Query("select  s from Store s where s.owner.id = :id")
    Optional<Store> findByMemberId(Long id);
}
