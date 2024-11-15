package poomasi.global.sqs;

import io.awspring.cloud.sqs.operations.SendResult;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

@Component
public class AmazonSQSSender {
    @Autowired
    private final SqsTemplate template;

    public AmazonSQSSender(SqsAsyncClient sqsAsyncClient) {
        this.template = SqsTemplate.newTemplate(sqsAsyncClient);
    }

    public SendResult<String> sendMessage(String queueName, String groupId, String message)
            throws InterruptedException {

        Thread.sleep(5000);

        return template.send(to -> to
                .queue(queueName)
                .messageGroupId(groupId)
                .messageDeduplicationId(groupId)
                .payload(message));
    }

}