package poomasi.domain.aftersales.service;


import org.springframework.stereotype.Service;
import poomasi.domain.aftersales.dto.cancel.request.FarmCancelRequest;

@Service
public class FarmAfterSalesService {

    public String farmCancel(FarmCancelRequest farmCancelRequest){
        return "success!";
    }

}
