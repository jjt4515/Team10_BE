package poomasi.domain.order._payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import poomasi.domain.order._payment.entity.Payment;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
//    public Long countByImpuidContainsIgnoreCase(String impuid);
    //List<Payment> findByOrderId(Long orderId);
}
