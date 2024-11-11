package poomasi.domain.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import poomasi.domain.order.entity.OrderProductDetails;

import java.util.List;

public interface OrderProductDetailsRepository extends JpaRepository<OrderProductDetails, Long> {
    List<OrderProductDetails> findByOrderId(Long orderId);
}
