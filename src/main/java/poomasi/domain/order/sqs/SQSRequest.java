package poomasi.domain.order.sqs;

public record SQSRequest(
        String merchantUid,
        String type
) {

}
