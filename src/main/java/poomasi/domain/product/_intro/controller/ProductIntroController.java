package poomasi.domain.product._intro.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import poomasi.domain.product._intro.dto.ProductIntroRequest;
import poomasi.domain.product._intro.dto.ProductIntroResponse;
import poomasi.domain.product._intro.service.ProductIntroService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/product/{productId}/intro")
public class ProductIntroController {

    private final ProductIntroService productIntroService;

    @Secured("ROLE_FARMER")
    @GetMapping("")
    public ResponseEntity<?> getIntro(@PathVariable Long productId) {
        ProductIntroResponse productIntro = productIntroService.getIntro(productId);
        return ResponseEntity.ok().body(productIntro);
    }

    @Secured("ROLE_FARMER")
    @PutMapping("")
    public ResponseEntity<?> updateIntro(
            @RequestBody ProductIntroRequest productIntroRequest,
            @PathVariable Long productId) {
        productIntroService.updateIntro(productIntroRequest, productId);
        return ResponseEntity.ok().build();
    }
}
