package poomasi.payment.util;


import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.CancelData;
import com.siot.IamportRestClient.request.PrepareData;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import jakarta.persistence.DiscriminatorColumn;
import jdk.jfr.Description;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import poomasi.global.error.ApplicationException;
import poomasi.payment.entity.ItemType;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;

import static org.hibernate.query.sqm.tree.SqmNode.log;
import static poomasi.global.error.ApplicationError.PAYMENT_INVALID_REQUEST;

@Component
public class PaymentUtil {

    private final IamportClient iamportClient;

    @Autowired
    public PaymentUtil(IamportClient iamportClient) {
        this.iamportClient = iamportClient;
    }

    @Description("merchantUid 생성")
    public String createMerchantUid(ItemType type) {
        if (type == ItemType.PRODUCT) {
            return "p" + new Date().getTime();
        }
        return "f" + new Date().getTime();
    }

    @Description("Product인지 Farm인지 확인")
    public ItemType checkItemType(String merchantUid) {
        if (merchantUid.startsWith("p")) {
            return ItemType.PRODUCT;
        }
        return ItemType.FARM;
    }

    @Description("포트원에서 결제 금액 조회하는 메서드")
    public BigDecimal getPaymentAmount(String impUid) {
        IamportResponse<Payment> iamportResponse = getSingleTransaction(impUid);
        return iamportResponse.getResponse().getAmount();
    }

    @Description("단건 결제 조회 API")
    public IamportResponse<Payment> getSingleTransaction(String impUid) {
        try {
            IamportResponse<Payment> iamportResponse = iamportClient.paymentByImpUid(impUid);
            return iamportResponse;
        } catch (IamportResponseException | IOException e) {
            log.error(e.getMessage(), e);
        }
        throw new ApplicationException(PAYMENT_INVALID_REQUEST);
    }

    @Description("결제 취소 api")
    public void cancelPaymentByImpUid(String impUid) {
        CancelData cancelDate = new CancelData(impUid, false);
        try {
            iamportClient.cancelPaymentByImpUid(cancelDate);
        } catch (IamportResponseException | IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    @Transactional
    @Description("imp uid로 결제 부분 환불 api 호출")
    public void partialRefundByImpUid(String impUid, BigDecimal checkSum, BigDecimal amount) {
        CancelData cancelData = new CancelData(impUid, true, amount);
        cancelData.setChecksum(checkSum);
        try {
            iamportClient.cancelPaymentByImpUid(cancelData);
        } catch (IOException e) {
            log.error("iamport response exception : " + e.getMessage(), e);
        } catch (IamportResponseException e) {
            log.error("iamport exception : " + e.getMessage(), e);
        }
    }


    @Transactional
    @Description("merchant Uid로 결제 부분 환불 api 호출")
    public void partialRefundByMerchantUid(String merchantUid, BigDecimal checkSum, BigDecimal amount, String reason) throws IOException, IamportResponseException {
        CancelData cancelData = new CancelData(merchantUid, false, amount);
        cancelData.setChecksum(checkSum);
        cancelData.setReason(reason);
        iamportClient.cancelPaymentByImpUid(cancelData);
    }


    @Description("사전 결제 데이터 전송")
    public void sendPrepareData(String merchantUid, BigDecimal amount) {
        PrepareData prepareData = this.generatePrepareData(merchantUid, amount);
        try {
            iamportClient.postPrepare(prepareData);
        } catch (IOException e1) {
            // TODO
        } catch (IamportResponseException e2) {
            // TODO
        }
    }

    @Description("단건 조회 후, 결제 되어야 할 금액과 결제 된 금액이 같은지 확인하는 메서드")
    public boolean validatePaymentAmount(String impUid, BigDecimal amountToBePaid) {
        IamportResponse<Payment> iamportResponse = getSingleTransaction(impUid); //내가 보냄
        BigDecimal amount = iamportResponse.getResponse().getAmount();
        return amountToBePaid.equals(amount);
    }

    @Description("사전 결제를 위한 Prepare Data를 만드는 메서드")
    private PrepareData generatePrepareData(String merchantUid, BigDecimal amount) {
        return new PrepareData(merchantUid, amount);
    }
}
