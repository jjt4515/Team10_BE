package poomasi.domain.store.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import poomasi.domain.store.dto.StoreRegisterRequest;
import poomasi.domain.store.service.StoreService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/store")
public class StoreController {

    private final StoreService storeService;

    @Secured("ROLE_FARMER")
    @PostMapping("")
    public ResponseEntity<?> addStore(@RequestBody StoreRegisterRequest storeRegisterRequest) {
        storeService.addStore(storeRegisterRequest);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{memberId}")
    public ResponseEntity<?> getStore(@PathVariable Long memberId) {
        return ResponseEntity.ok(storeService.getStore(memberId));
    }

    @Secured("ROLE_FARMER")
    @PutMapping("")
    public ResponseEntity<?> updateStore(@RequestBody StoreRegisterRequest storeRegisterRequest) {
        storeService.updateStore(storeRegisterRequest);
        return ResponseEntity.ok().build();
    }
}
