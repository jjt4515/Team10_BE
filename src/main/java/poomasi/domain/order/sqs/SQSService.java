package poomasi.domain.order.sqs;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import poomasi.global.error.BusinessError;
import poomasi.global.error.BusinessException;

@Service
@RequiredArgsConstructor
public class SQSService {
    private final AmazonSQSSender amazonSQSSender;
    @Value("${spring.cloud.aws.sqs.queue-name}")
    String queueName;

    public void sendMessage(SQSRequest sqsRequest) throws InterruptedException {
        HashMap<String,Object> message = new HashMap<>();
        message.put("mid",sqsRequest.merchantUid());
        message.put("type",sqsRequest.type());

        try {
            // ObjectMapper를 이용해 HashMap을 JSON 문자열로 변환
            ObjectMapper objectMapper = new ObjectMapper();
            String messageBody = objectMapper.writeValueAsString(message);
            amazonSQSSender.sendMessage(queueName, UUID.randomUUID().toString(), messageBody);
        } catch (Exception e) {
            throw new BusinessException(BusinessError.SQS_ERROR);
        }

    }
}
