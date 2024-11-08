package poomasi.domain.order._aftersales.service;


import com.siot.IamportRestClient.IamportClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import poomasi.domain.order._aftersales.repository.ProductAfterSalesRepository;

@Service
@RequiredArgsConstructor
public class RefundService {

    private final ProductAfterSalesRepository productAfterSalesRepository;
    private final IamportClient iamportClient;


   /* public void refundFull(){
        iamportClient.
    }*/


}
