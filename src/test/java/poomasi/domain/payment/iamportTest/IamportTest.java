package poomasi.domain.payment.iamportTest;

import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.PrepareData;
import com.siot.IamportRestClient.response.AccessToken;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import com.siot.IamportRestClient.response.Prepare;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;
import poomasi.domain.order._payment.dto.request.PaymentPreRegisterRequest;
import poomasi.domain.order._payment.dto.response.PaymentPreRegisterResponse;


import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.fasterxml.jackson.databind.type.LogicalType.DateTime;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class IamportTest {

    private IamportClient iamportClient;

    private String apiKey="~";
    private String secretKey="~";
    private String accessToken;

    @BeforeEach
    public void setUp() throws Exception {
        this.iamportClient = new IamportClient(apiKey, secretKey);
        IamportResponse<AccessToken> auth_response = iamportClient.getAuth();
        this.accessToken=auth_response.getResponse().getToken();
    }

    @Test
    public void portonePrePaymentRegister_Test() throws IamportResponseException ,IOException{
        String merchantUid = "poomasi_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        BigDecimal amount = new BigDecimal("100");
        PrepareData prepareData = new PrepareData(merchantUid, amount);

        System.out.println(merchantUid);
        IamportResponse<Prepare> prepareIamportResponse = iamportClient.postPrepare(prepareData);
        System.out.println("Response Code: " + prepareIamportResponse.getCode());
        System.out.println("Response Message: " + prepareIamportResponse.getMessage());

    }

}

