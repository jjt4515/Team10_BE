package poomasi.domain.order.sqs;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequiredArgsConstructor
public class SQSController {

    private final SQSService sqsService;

    @PostMapping("/api/sqs")
    public ResponseEntity<?> sqs(@RequestBody SQSRequest sqsRequest) throws InterruptedException {
        sqsService.sendMessage(sqsRequest);
        return ResponseEntity.ok().build();
    }
}
