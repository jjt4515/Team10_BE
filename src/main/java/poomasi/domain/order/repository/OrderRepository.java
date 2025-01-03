package poomasi.domain.order.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import poomasi.domain.order.entity.Order;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByMerchantUid(String merchantUid);
    List<Order> findByMemberId(Long memberId);
    //Page<Order> findByMemberId(Long memberId, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.updateAt BETWEEN :startDate AND :endDate")
    List<Order> findAllByUpdateAtBetween(@Param("startDate") LocalDateTime startDate,
                                         @Param("endDate") LocalDateTime endDate);
}
