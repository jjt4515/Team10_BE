package poomasi.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import poomasi.payment.entity.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
//    public Long countByImpuidContainsIgnoreCase(String impuid);
    //List<Payment> findByOrderId(Long orderId);
    Payment findByImpUid(String impUid);
}
