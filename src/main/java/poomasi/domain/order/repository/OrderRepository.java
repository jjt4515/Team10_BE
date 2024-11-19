package poomasi.domain.order.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import poomasi.domain.order.entity.Order;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByMerchantUid(String merchantUid);
    Page<Order> findById(Long id, Pageable pageable);
    List<Order> findByMemberId(Long memberId);
    //Page<Order> findByMemberId(Long memberId, Pageable pageable);
}
