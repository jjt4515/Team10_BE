package poomasi.domain.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import poomasi.domain.order.entity.Order;
import poomasi.domain.order.entity.OrderProductDetails;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByMemberId(Long memberId);
    //List<Order> findById(Long id);
    Optional<Order> findByMerchantUid(String merchantUid);
}
