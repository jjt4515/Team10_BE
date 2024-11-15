package poomasi.domain.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import poomasi.domain.store.entity.Store;

import java.util.Optional;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {

    //@Query("select  s from Store s where s.owner.farmId = :farmId")
    Optional<Store> findByOwnerId(Long id);
}
