package poomasi.global.sqs;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.converter.json.GsonBuilderUtils;
import org.springframework.stereotype.Service;
import poomasi.global.error.BusinessError;
import poomasi.global.error.BusinessException;

@Service
@RequiredArgsConstructor
@Slf4j
public class SQSService {
    private final AmazonSQSSender amazonSQSSender;
    @Value("${spring.cloud.aws.sqs.queue-name}")
    String queueName;

    public void sendMessage(SQSRequest sqsRequest) throws InterruptedException {
        log.info("sqs~");

        HashMap<String,Object> message = new HashMap<>();
        String merchant_uid = sqsRequest.merchantUid();
        message.put("mid", sqsRequest.merchantUid());
        message.put("type",sqsRequest.type());
        
        //일단은 두 번째로 일로 옴
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
