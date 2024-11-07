package poomasi.domain.product._store.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import poomasi.domain.product._store.dto.StoreFeeRequest;
import poomasi.domain.product._store.dto.StoreRegisterRequest;
import poomasi.domain.product._store.service.StoreService;

@Controller
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;

    @Secured("ROLE_FARMER")
    @PostMapping("/api/store")
    public ResponseEntity<?> addStore(@RequestBody StoreRegisterRequest storeRegisterRequest) {
        storeService.addStore(storeRegisterRequest);
        return ResponseEntity.ok().build();
    }

    @Secured("ROLE_FARMER")
    @GetMapping("/api/store")
    public ResponseEntity<?> getStore() {
        return ResponseEntity.ok(storeService.getStore());
    }

    @Secured("ROLE_FARMER")
    @PutMapping("/api/store")
    public ResponseEntity<?> updateStore(@RequestBody StoreRegisterRequest storeRegisterRequest) {
        storeService.updateStore(storeRegisterRequest);
        return ResponseEntity.ok().build();
    }

    @Secured("ROLE_FARMER")
    @PatchMapping("/api/store")
    public ResponseEntity<?> updateFee(@RequestBody StoreFeeRequest storeFeeRequest) {
        storeService.updateFee(storeFeeRequest);
        return ResponseEntity.ok().build();
    }

}
