package poomasi.domain.payment.iamportTest;

import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.PrepareData;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Prepare;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.yml")
public class IamportTest {
    private IamportClient iamportClient;

    @Value("${imp.api.key}")
    private String apiKey;

    @Value("${imp.api.secretKey}")
    private String secretKey;


    @BeforeEach
    public void setUp() throws Exception {
        this.iamportClient = new IamportClient(apiKey, secretKey);
    }

    @Test
    public void portonePrePaymentRegister_Test() throws IamportResponseException, IOException {
        String merchantUid = "poomasi_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        BigDecimal amount = new BigDecimal("100");
        PrepareData prepareData = new PrepareData(merchantUid, amount);

        // System.out.println(merchantUid);
        IamportResponse<Prepare> prepareIamportResponse = iamportClient.postPrepare(prepareData);
        System.out.println("Response Code: " + prepareIamportResponse.getCode());
        System.out.println("Response Message: " + prepareIamportResponse.getMessage());

    }

}

