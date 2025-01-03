package poomasi.payment.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PaymentScheduler {

    private final PaymentPortoneService paymentPortoneService;

    public PaymentScheduler(PaymentPortoneService paymentPortoneService) {
        this.paymentPortoneService = paymentPortoneService;
    }

    @Scheduled(fixedRate = 180000) // 3분마다 실행
    public void checkAndCancelExpiredPayments() {
        paymentPortoneService.cancelExpiredPendingPayments();
    }
}
