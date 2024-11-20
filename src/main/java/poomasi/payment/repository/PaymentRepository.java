package poomasi.payment.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import poomasi.payment.entity.Payment;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Payment findByImpUid(String impUid);

    @Query("SELECT p FROM Payment p WHERE p.paymentStatus = 'PAYMENT_PENDING' AND p.createdAt <= :timeLimit")
    List<Payment> findPendingPaymentsOlderThan(@Param("timeLimit") LocalDateTime timeLimit);
}
