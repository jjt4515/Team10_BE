package poomasi.domain.order.sqs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.sqs.annotation.SqsListener;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import poomasi.domain.order.repository.OrderedProductRepository;
import poomasi.domain.order.repository.ProductOrderRepository;
import poomasi.domain.reservation.entity.Reservation;
import poomasi.domain.reservation.repository.ReservationRepository;
import poomasi.payment.service.PaymentPortoneService;
import poomasi.payment.service.PaymentService;

@Component
@RequiredArgsConstructor
public class TransferListener {
    private final ProductOrderRepository productOrderRepository;
    private final ReservationRepository reservationRepository;
    private final PaymentPortoneService paymentService;

    @SqsListener("${spring.cloud.aws.sqs.queue-name}")
    public void messageListener(String message) throws JsonProcessingException {
        //System.out.println("Listener: " + message);
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> parsedMap = objectMapper.readValue(message, Map.class);

        String mid = (String) parsedMap.get("mid");  // id는 Long 타입으로 캐스팅
        String type = (String) parsedMap.get("type");

        if(type.equals("product")){
            productOrderRepository.findByMerchantUid(mid).ifPresent(productOrder -> {
                String impUid =  productOrder.getPayment().getImpUid();
                String status = paymentService.getPayment(impUid);
                paymentService.confirmProductPayment(productOrder, status);
            });
        }else{
//            reservationRepository.findByMerchantUid(mid).ifPresent(reservation -> {
//                String impUid = reservation.getPayment().getImpUid();
//                String status = paymentService.getPayment(impUid);
//            });
        }

    }

}